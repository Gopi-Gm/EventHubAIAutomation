package com.api.ai.orchestration;

/**
 * Input context passed to the AI orchestrator when a test failure is detected.
 *
 * Contains all information needed to build a meaningful AI prompt:
 *  - scenarioName : the TestNG test method name / description
 *  - featureUri   : the test class name or feature file path
 *  - errorMessage : the assertion or exception message
 *  - stackTrace   : abbreviated stack trace (truncated to avoid token waste)
 */
public class FailureContext {

    private final String scenarioName;
    private final String featureUri;
    private final String errorMessage;
    private final String stackTrace;

    public FailureContext(String scenarioName, String featureUri,
                          String errorMessage, String stackTrace) {
        this.scenarioName = scenarioName;
        this.featureUri   = featureUri;
        this.errorMessage = errorMessage;
        this.stackTrace   = stackTrace;
    }

    public String getScenarioName() { return scenarioName; }
    public String getFeatureUri()   { return featureUri; }
    public String getErrorMessage() { return errorMessage; }
    public String getStackTrace()   { return stackTrace; }

    /**
     * Builds the stack trace from a {@link Throwable}, truncated to the first
     * {@code maxLines} lines to avoid overly large prompts.
     *
     * @param t        the throwable
     * @param maxLines maximum number of stack trace lines to include
     * @return abbreviated stack trace string
     */
    public static String abbreviateStackTrace(Throwable t, int maxLines) {
        if (t == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(t.getClass().getName()).append(": ").append(t.getMessage()).append("\n");
        StackTraceElement[] elements = t.getStackTrace();
        int limit = Math.min(elements.length, maxLines);
        for (int i = 0; i < limit; i++) {
            sb.append("\tat ").append(elements[i]).append("\n");
        }
        if (elements.length > limit) {
            sb.append("\t... ").append(elements.length - limit).append(" more\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "FailureContext{" +
                "scenario='" + scenarioName + '\'' +
                ", featureUri='" + featureUri + '\'' +
                '}';
    }
}
