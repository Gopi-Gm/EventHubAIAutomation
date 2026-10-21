package com.api.ai.verify;

import com.api.ai.orchestration.AiOrchestrator;
import com.api.ai.orchestration.FailureContext;
import com.api.ai.analysis.FailureAnalysisResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * Advisory API response verifier.
 *
 * Provides semantic analysis when a deterministic assertion on a response field
 * fails. This class does NOT change test results — tests still fail based on
 * TestNG assertions. It only attaches advisory context logged to the console
 * (and optionally to reports) to help developers understand the mismatch.
 *
 * Typical usage in a test or service class:
 * <pre>{@code
 *   try {
 *     Assert.assertEquals(actualName, expectedName, "PartyName mismatch");
 *   } catch (AssertionError e) {
 *     ApiResponseVerifier.adviseOnMismatch("PartyName", expectedName, actualName, response.asString());
 *     throw e; // keep deterministic failure
 *   }
 * }</pre>
 */
public class ApiResponseVerifier {

    private static final Logger logger = LogManager.getLogger(ApiResponseVerifier.class);

    private ApiResponseVerifier() {}

    /**
     * Requests an AI advisory when a field assertion mismatch occurs.
     *
     * Behavior:
     *  - Builds a FailureContext describing the mismatch
     *  - Calls AiOrchestrator.analyzeFailure() (advisory — does not throw)
     *  - Logs the analysis result if available
     *
     * @param fieldName    the name of the response field that mismatched
     * @param expected     the expected value
     * @param actual       the actual value received
     * @param responseBody the raw response body (for context)
     */
    public static void adviseOnMismatch(String fieldName,
                                        String expected,
                                        String actual,
                                        String responseBody) {
        String scenarioName = "API Mismatch: " + fieldName;
        String featureUri   = "ApiResponseVerifier";
        String errorMessage = String.format(
                "Field '%s': expected='%s', actual='%s'", fieldName, expected, actual);
        String detail = "Response body (first 500 chars):\n" +
                (responseBody != null && responseBody.length() > 500
                        ? responseBody.substring(0, 500) + "..."
                        : responseBody);

        FailureContext context = new FailureContext(scenarioName, featureUri,
                errorMessage, detail);

        logger.warn("[ApiResponseVerifier] Mismatch on field '{}': expected='{}', actual='{}'",
                fieldName, expected, actual);

        Optional<FailureAnalysisResult> advisory = AiOrchestrator.analyzeFailure(context);

        advisory.ifPresent(result -> {
            logger.info("[ApiResponseVerifier] AI Advisory for '{}' mismatch:\n" +
                            "  Category    : {}\n" +
                            "  Summary     : {}\n" +
                            "  Likely Cause: {}\n" +
                            "  Suggestions : {}",
                    fieldName,
                    result.getResolvedCategory(),
                    result.getSummary(),
                    result.getLikelyCause(),
                    result.getSuggestedChecks());
        });
    }
}
