package com.api.ai.orchestration;

import com.api.ai.analysis.FailureAnalysisResult;
import com.api.ai.analysis.FailureAnalyzer;
import com.api.ai.client.OpenAiClient;
import com.api.ai.config.AiConfig;
import com.api.ai.runtime.AiRuntimeBudget;
import com.api.ai.security.AiPromptSanitizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Optional;

/**
 * Central single entry point for all runtime AI calls.
 *
 * {@link #analyzeFailure(FailureContext)} steps:
 *   1. Guardrails — check isEnabled() and tryAcquire() budget
 *   2. Prompt construction — build a structured prompt from the FailureContext
 *   3. Sanitization — mask secrets via AiPromptSanitizer
 *   4. OpenAI call — POST to Chat Completions API
 *   5. Parse + normalize — Jackson JSON → FailureAnalysisResult, then FailureAnalyzer
 *   6. Audit log — append a small JSONL line to target/ai-analysis/ai-audit.jsonl
 *   7. Return Optional<FailureAnalysisResult>
 */
public class AiOrchestrator {

    private static final Logger logger = LogManager.getLogger(AiOrchestrator.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String AUDIT_FILE = "target/ai-analysis/ai-audit.jsonl";

    private static final String SYSTEM_MESSAGE =
            "You are an expert API test failure analyst. When given a test failure, " +
            "respond ONLY with a valid JSON object matching this schema:\n" +
            "{\n" +
            "  \"scenario\": \"<test name>\",\n" +
            "  \"category\": \"<one of: AUTH, DATA_SETUP, API_CONTRACT, ASSERTION, ENVIRONMENT, TIMING, OTHER>\",\n" +
            "  \"summary\": \"<1-2 sentences explaining what failed>\",\n" +
            "  \"likelyCause\": \"<single most likely root cause>\",\n" +
            "  \"suggestedChecks\": [\"<check 1>\", \"<check 2>\", \"<check 3>\"]\n" +
            "}\n" +
            "Do NOT include any text outside the JSON object. Do NOT wrap in markdown code blocks.";

    private AiOrchestrator() {}

    /**
     * Analyzes a test failure using AI.
     *
     * @param context the failure context
     * @return Optional containing the analysis result, or empty if AI is disabled/failed
     */
    public static Optional<FailureAnalysisResult> analyzeFailure(FailureContext context) {
        // --- 1. Guardrails ---
        if (!AiConfig.isEnabled()) {
            logger.debug("[AiOrchestrator] AI is disabled — skipping analysis for '{}'.",
                    context.getScenarioName());
            return Optional.empty();
        }

        if (!AiRuntimeBudget.tryAcquire()) {
            return Optional.empty();
        }

        try {
            // --- 2. Prompt construction ---
            String userMessage = buildPrompt(context);

            // --- 3. Sanitization ---
            String sanitizedPrompt = AiPromptSanitizer.sanitize(userMessage);

            // --- 4. OpenAI call ---
            logger.info("[AiOrchestrator] Sending failure '{}' to AI for analysis.",
                    context.getScenarioName());
            String rawResponse = OpenAiClient.createChatCompletion(SYSTEM_MESSAGE, sanitizedPrompt);

            if (rawResponse == null || rawResponse.isBlank()) {
                logger.warn("[AiOrchestrator] AI returned no content for '{}'.",
                        context.getScenarioName());
                appendAuditLog(context, "NO_CONTENT");
                return Optional.empty();
            }

            // --- 5. Parse + normalize ---
            String jsonContent = extractJson(rawResponse);
            FailureAnalysisResult result = MAPPER.readValue(jsonContent, FailureAnalysisResult.class);
            FailureAnalyzer.postProcess(context, result);

            // --- 6. Audit log ---
            appendAuditLog(context, result.getResolvedCategory().name());

            logger.info("[AiOrchestrator] Analysis complete for '{}': category={}, likelyCause='{}'",
                    result.getScenario(), result.getResolvedCategory(), result.getLikelyCause());

            return Optional.of(result);

        } catch (Exception e) {
            logger.warn("[AiOrchestrator] AI analysis failed for '{}': {}",
                    context.getScenarioName(), e.getMessage());

            if (!AiConfig.isFailOpen()) {
                throw new IllegalStateException(
                        "[AiOrchestrator] AI analysis failed and fail-open=false. " +
                        "Scenario: " + context.getScenarioName(), e);
            }

            appendAuditLog(context, "ERROR");
            return Optional.empty();
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static String buildPrompt(FailureContext ctx) {
        return "Test Scenario: " + ctx.getScenarioName() + "\n" +
               "Feature/Class: " + ctx.getFeatureUri() + "\n" +
               "Error Message:\n" + ctx.getErrorMessage() + "\n\n" +
               "Stack Trace (abbreviated):\n" + ctx.getStackTrace() + "\n\n" +
               "Analyze this failure and respond with JSON only.";
    }

    /**
     * Extracts a JSON object string from the AI response.
     * Handles cases where the model wraps output in ```json ... ``` blocks.
     */
    private static String extractJson(String raw) {
        String trimmed = raw.trim();
        // Strip markdown code fences if present
        if (trimmed.startsWith("```")) {
            int firstBrace = trimmed.indexOf('{');
            int lastBrace  = trimmed.lastIndexOf('}');
            if (firstBrace >= 0 && lastBrace >= 0) {
                return trimmed.substring(firstBrace, lastBrace + 1);
            }
        }
        return trimmed;
    }

    private static void appendAuditLog(FailureContext context, String outcome) {
        try {
            File auditFile = new File(AUDIT_FILE);
            auditFile.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(auditFile, true))) {
                String entry = String.format(
                        "{\"timestamp\":\"%s\",\"scenario\":\"%s\",\"outcome\":\"%s\"}",
                        Instant.now().toString(),
                        escape(context.getScenarioName()),
                        escape(outcome));
                pw.println(entry);
            }
        } catch (IOException e) {
            logger.warn("[AiOrchestrator] Could not write to audit log: {}", e.getMessage());
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
