package com.api.ai.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central configuration for the AI analysis layer.
 *
 * Reads settings from (in priority order):
 *   1. Environment variables (AGR_AI_ENABLED, OPENAI_API_KEY)
 *   2. System properties (-Dagr.ai.enabled, -Dopenai.api.key)
 *   3. test-config.properties on the classpath
 *
 * AI is effectively enabled only when BOTH:
 *   - agr.ai.enabled = true
 *   - OPENAI_API_KEY is present and non-blank
 *
 * When disabled, all AI components are silent no-ops.
 */
public class AiConfig {

    private static final Logger logger = LogManager.getLogger(AiConfig.class);

    // Property / env-var keys
    public static final String PROP_ENABLED        = "agr.ai.enabled";
    public static final String PROP_FAIL_OPEN      = "agr.ai.fail-open";
    public static final String PROP_MAX_CALLS      = "agr.ai.budget.max-calls-per-run";
    public static final String PROP_MODEL          = "agr.ai.model";
    public static final String PROP_API_KEY        = "openai.api.key";   // system property
    public static final String ENV_API_KEY         = "OPENAI_API_KEY";   // environment variable
    public static final String ENV_AI_ENABLED      = "AGR_AI_ENABLED";

    // Defaults
    public static final String DEFAULT_MODEL       = "gpt-4o-mini";
    public static final int    DEFAULT_MAX_CALLS   = 20;
    public static final String OPENAI_CHAT_URL     =
            "https://api.openai.com/v1/chat/completions";

    /** The singleton model field — read once per JVM run */
    public static final String MODEL;
    private static final boolean ENABLED;
    private static final boolean FAIL_OPEN;
    private static final int    MAX_CALLS_PER_RUN;
    private static final String API_KEY;

    static {
        Properties props = loadProperties();
        API_KEY          = resolveApiKey();
        ENABLED          = resolveBoolean(props, PROP_ENABLED, ENV_AI_ENABLED, false)
                           && API_KEY != null && !API_KEY.isBlank();
        FAIL_OPEN        = resolveBoolean(props, PROP_FAIL_OPEN, null, true);
        MAX_CALLS_PER_RUN = resolveInt(props, PROP_MAX_CALLS, DEFAULT_MAX_CALLS);
        MODEL            = resolveString(props, PROP_MODEL, DEFAULT_MODEL);

        logger.info("[AiConfig] AI enabled={}, model={}, maxCalls={}, failOpen={}",
                ENABLED, MODEL, MAX_CALLS_PER_RUN, FAIL_OPEN);
    }

    private AiConfig() {}

    public static boolean isEnabled()       { return ENABLED; }
    public static boolean isFailOpen()      { return FAIL_OPEN; }
    public static int     getMaxCallsPerRun(){ return MAX_CALLS_PER_RUN; }
    public static String  getModel()        { return MODEL; }
    public static String  getApiKey()       { return API_KEY; }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = AiConfig.class.getClassLoader()
                .getResourceAsStream("test-config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            logger.warn("[AiConfig] Could not load test-config.properties: {}", e.getMessage());
        }
        return props;
    }

    private static String resolveApiKey() {
        // Env var takes priority
        String val = System.getenv(ENV_API_KEY);
        if (val != null && !val.isBlank()) return val;
        // Then system property
        val = System.getProperty(PROP_API_KEY);
        if (val != null && !val.isBlank()) return val;
        return null;
    }

    private static boolean resolveBoolean(Properties props, String propKey,
                                          String envKey, boolean defaultVal) {
        if (envKey != null) {
            String envVal = System.getenv(envKey);
            if (envVal != null) return Boolean.parseBoolean(envVal.trim());
        }
        String sysVal = System.getProperty(propKey);
        if (sysVal != null) return Boolean.parseBoolean(sysVal.trim());
        String propVal = props.getProperty(propKey);
        if (propVal != null) return Boolean.parseBoolean(propVal.trim());
        return defaultVal;
    }

    private static int resolveInt(Properties props, String propKey, int defaultVal) {
        String sysVal = System.getProperty(propKey);
        if (sysVal != null) { try { return Integer.parseInt(sysVal.trim()); } catch (NumberFormatException ignored) {} }
        String propVal = props.getProperty(propKey);
        if (propVal != null) { try { return Integer.parseInt(propVal.trim()); } catch (NumberFormatException ignored) {} }
        return defaultVal;
    }

    private static String resolveString(Properties props, String propKey, String defaultVal) {
        String sysVal = System.getProperty(propKey);
        if (sysVal != null && !sysVal.isBlank()) return sysVal.trim();
        String propVal = props.getProperty(propKey);
        if (propVal != null && !propVal.isBlank()) return propVal.trim();
        return defaultVal;
    }
}
