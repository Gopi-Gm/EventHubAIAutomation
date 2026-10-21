package com.api.ai.report;

import com.api.ai.analysis.FailureAnalysisResult;
import com.api.ai.analysis.RunSummaryGenerator;
import com.api.ai.orchestration.AiOrchestrator;
import com.api.ai.orchestration.FailureContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * TestNG listener that hooks into test failures to trigger AI analysis.
 *
 * Register this listener in suite.xml:
 *   {@code <listener class-name="com.api.ai.report.AiReportEnhancer"/>}
 *
 * Behavior on test failure (onTestFailure):
 *  - Constructs a FailureContext from the ITestResult
 *  - Calls AiOrchestrator.analyzeFailure()
 *  - Stores result in-memory
 *
 * Behavior on suite finish (onFinish):
 *  - Writes target/ai-analysis/ai-report.json
 *  - Calls AiDashboardWriter to produce target/ai-analysis/ai-dashboard.html
 */
public class AiReportEnhancer implements ITestListener {

    private static final Logger logger = LogManager.getLogger(AiReportEnhancer.class);
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static final String REPORT_DIR  = "target/ai-analysis";
    private static final String REPORT_JSON = REPORT_DIR + "/ai-report.json";

    // Thread-safe list — tests may run in parallel
    private final List<FailureAnalysisResult> analysisResults =
            Collections.synchronizedList(new ArrayList<>());

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable throwable = result.getThrowable();
        String scenarioName = result.getMethod().getMethodName();
        String featureUri   = result.getTestClass().getName();
        String errorMessage = throwable != null ? throwable.getMessage() : "No error message";
        String stackTrace   = FailureContext.abbreviateStackTrace(throwable, 15);

        FailureContext context = new FailureContext(scenarioName, featureUri,
                errorMessage, stackTrace);

        logger.info("[AiReportEnhancer] Test failed: '{}' — triggering AI analysis.", scenarioName);

        Optional<FailureAnalysisResult> aiResult = AiOrchestrator.analyzeFailure(context);
        aiResult.ifPresent(r -> {
            analysisResults.add(r);
            logger.info("[AiReportEnhancer] AI analysis stored for '{}'.", scenarioName);
        });
    }

    @Override
    public void onFinish(ITestContext context) {
        List<FailureAnalysisResult> snapshot;
        synchronized (analysisResults) {
            snapshot = new ArrayList<>(analysisResults);
        }

        if (snapshot.isEmpty()) {
            logger.info("[AiReportEnhancer] No AI analysis results to write.");
            return;
        }

        try {
            // Ensure output directory exists
            new File(REPORT_DIR).mkdirs();

            // Write ai-report.json
            MAPPER.writeValue(new File(REPORT_JSON), snapshot);
            logger.info("[AiReportEnhancer] AI report written to {}", REPORT_JSON);

            // Generate run summary
            String summary = RunSummaryGenerator.generateSummary(snapshot);

            // Write ai-dashboard.html
            AiDashboardWriter.write(REPORT_DIR, summary, snapshot);

        } catch (IOException e) {
            logger.warn("[AiReportEnhancer] Could not write AI report: {}", e.getMessage());
        }
    }

    // --- Unused lifecycle methods (kept for interface completeness) ---

    @Override public void onStart(ITestContext context) {}
    @Override public void onTestStart(ITestResult result) {}
    @Override public void onTestSuccess(ITestResult result) {}
    @Override public void onTestSkipped(ITestResult result) {}
}
