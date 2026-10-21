package com.api.ai.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Sanitizes AI prompts before they are sent to OpenAI.
 *
 * Design goals:
 *  - Best-effort redaction of secrets
 *  - No secrets (auth tokens, client secrets) should appear in prompts or logs
 *
 * Currently masks:
 *  - Authorization Bearer tokens:  "Authorization: Bearer <token>"
 *  - TestConfig CLIENT_SECRET values embedded in stack traces / messages
 *  - Generic password-like fields: "password=...", "secret=..."
 */
public class AiPromptSanitizer {

    private static final Logger logger = LogManager.getLogger(AiPromptSanitizer.class);

    // Regex patterns for redaction
    private static final String BEARER_PATTERN   = "(?i)(Authorization:\\s*Bearer\\s+)\\S+";
    private static final String BEARER_REPLACE   = "$1[REDACTED]";

    private static final String SECRET_PATTERN   = "(?i)(client_secret|password|secret|api[-_]?key)\\s*[=:]\\s*\\S+";
    private static final String SECRET_REPLACE   = "$1=[REDACTED]";

    private static final String OPENAI_KEY_PATTERN = "sk-[A-Za-z0-9-_]{20,}";
    private static final String OPENAI_KEY_REPLACE = "[OPENAI_KEY_REDACTED]";

    private AiPromptSanitizer() {}

    /**
     * Sanitizes the given prompt text, masking known secret patterns.
     *
     * @param prompt raw prompt text (may contain stack traces with secrets)
     * @return sanitized prompt safe to send to OpenAI
     */
    public static String sanitize(String prompt) {
        if (prompt == null || prompt.isBlank()) return prompt;

        String sanitized = prompt
                .replaceAll(BEARER_PATTERN, BEARER_REPLACE)
                .replaceAll(SECRET_PATTERN, SECRET_REPLACE)
                .replaceAll(OPENAI_KEY_PATTERN, OPENAI_KEY_REPLACE);

        if (!sanitized.equals(prompt)) {
            logger.debug("[AiPromptSanitizer] Prompt was sanitized — secrets were redacted.");
        }
        return sanitized;
    }
}
