package com.api.ai.analysis;

/**
 * Failure category enum for classifying AI-analyzed test failures.
 *
 * Categories:
 *  - AUTH          : Authentication or authorization failures (401, 403, token issues)
 *  - DATA_SETUP    : Test data missing, stale, or incorrectly configured
 *  - API_CONTRACT  : API response shape or field values differ from the contract
 *  - ASSERTION     : Test assertion is incorrect or overly strict
 *  - ENVIRONMENT   : Infrastructure, network, or environment issues
 *  - TIMING        : Race conditions, timeouts, eventual-consistency issues
 *  - OTHER         : Unclassifiable failures
 */
public enum FailureCategory {
    AUTH,
    DATA_SETUP,
    API_CONTRACT,
    ASSERTION,
    ENVIRONMENT,
    TIMING,
    OTHER;

    /**
     * Converts a raw string (from AI response) to a {@link FailureCategory},
     * falling back to {@link #OTHER} when the string does not match.
     *
     * @param raw the raw category string from AI
     * @return the matched enum constant, or {@link #OTHER}
     */
    public static FailureCategory fromString(String raw) {
        if (raw == null || raw.isBlank()) return OTHER;
        try {
            return FailureCategory.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}
