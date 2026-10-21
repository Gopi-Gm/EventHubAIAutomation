package com.api.ai.analysis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Aggregates all {@link FailureAnalysisResult} objects collected during a run
 * and produces a deterministic, human-readable run summary.
 *
 * No AI call is made here — the summary is built from the already-analyzed results.
 * This ensures the summary is always available even if the AI budget is exhausted.
 */
public class RunSummaryGenerator {

    private static final Logger logger = LogManager.getLogger(RunSummaryGenerator.class);

    private RunSummaryGenerator() {}

    /**
     * Generates a 1–3 sentence plain-English summary of all analyzed failures.
     *
     * @param results list of all {@link FailureAnalysisResult} collected during the run
     * @return summary string (never null)
     */
    public static String generateSummary(List<FailureAnalysisResult> results) {
        if (results == null || results.isEmpty()) {
            return "No AI-analyzed failures in this run.";
        }

        int total = results.size();

        // Count failures per resolved category
        Map<FailureCategory, Long> countByCategory = results.stream()
                .filter(r -> r.getResolvedCategory() != null)
                .collect(Collectors.groupingBy(FailureAnalysisResult::getResolvedCategory,
                        Collectors.counting()));

        // Build category breakdown string: "API_CONTRACT (3), AUTH (1)"
        String breakdown = countByCategory.entrySet().stream()
                .sorted(Map.Entry.<FailureCategory, Long>comparingByValue().reversed())
                .map(e -> e.getKey().name() + " (" + e.getValue() + ")")
                .collect(Collectors.joining(", "));

        // Determine the dominant category
        Optional<Map.Entry<FailureCategory, Long>> dominant = countByCategory.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        StringBuilder sb = new StringBuilder();
        sb.append("AI analyzed ").append(total)
          .append(total == 1 ? " failure" : " failures").append(" in this run.");

        if (!breakdown.isBlank()) {
            sb.append(" Category breakdown: ").append(breakdown).append(".");
        }

        dominant.ifPresent(d -> {
            if (d.getValue() > 1) {
                sb.append(" The most common failure type is ")
                  .append(d.getKey().name())
                  .append(" (").append(d.getValue()).append(" occurrences).")
                  .append(" Review the AI advisory cards below for suggested fixes.");
            }
        });

        logger.debug("[RunSummaryGenerator] Summary: {}", sb);
        return sb.toString();
    }
}
