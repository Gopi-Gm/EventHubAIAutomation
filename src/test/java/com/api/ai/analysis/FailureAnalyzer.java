package com.api.ai.analysis;

import com.api.ai.orchestration.FailureContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Post-processor for raw AI-returned {@link FailureAnalysisResult} objects.
 *
 * Responsibilities:
 *  - Normalize the raw category string to a {@link FailureCategory} enum value
 *  - Fill in the scenario name from {@link FailureContext} when AI omits it
 *  - Sanitize / trim string fields
 */
public class FailureAnalyzer {

    private static final Logger logger = LogManager.getLogger(FailureAnalyzer.class);

    private FailureAnalyzer() {}

    /**
     * Post-processes a parsed {@link FailureAnalysisResult} in place.
     *
     * @param context   the original failure context (used as fallback for scenario name)
     * @param result    the result to post-process
     * @return the same {@code result} object, mutated
     */
    public static FailureAnalysisResult postProcess(FailureContext context,
                                                    FailureAnalysisResult result) {
        if (result == null) return null;

        // Normalize category → enum
        FailureCategory resolved = FailureCategory.fromString(result.getCategory());
        result.setResolvedCategory(resolved);

        // If AI didn't populate scenario, use context
        if (result.getScenario() == null || result.getScenario().isBlank()) {
            if (context != null && context.getScenarioName() != null) {
                result.setScenario(context.getScenarioName());
            }
        }

        // Trim whitespace from all string fields
        if (result.getSummary()     != null) result.setSummary(result.getSummary().trim());
        if (result.getLikelyCause() != null) result.setLikelyCause(result.getLikelyCause().trim());
        if (result.getScenario()    != null) result.setScenario(result.getScenario().trim());

        logger.debug("[FailureAnalyzer] Post-processed result: scenario='{}', resolvedCategory={}",
                result.getScenario(), result.getResolvedCategory());

        return result;
    }
}
