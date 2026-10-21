package com.api.ai.runtime;

import com.api.ai.config.AiConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Per-run call budget for the AI analysis layer.
 *
 * Holds an AtomicInteger counter that is incremented on each AI call.
 * When the counter exceeds {@link AiConfig#getMaxCallsPerRun()}, further
 * calls are blocked and a warning is logged.
 *
 * This prevents runaway API costs when many tests fail simultaneously.
 */
public class AiRuntimeBudget {

    private static final Logger logger = LogManager.getLogger(AiRuntimeBudget.class);

    private static final AtomicInteger callCount = new AtomicInteger(0);

    private AiRuntimeBudget() {}

    /**
     * Try to acquire one AI call slot from the budget.
     *
     * @return {@code true} if the call is within the budget and may proceed;
     *         {@code false} if the budget is exhausted.
     */
    public static boolean tryAcquire() {
        int current = callCount.incrementAndGet();
        int max     = AiConfig.getMaxCallsPerRun();
        if (current > max) {
            logger.warn("[AiRuntimeBudget] AI call budget exhausted " +
                    "(limit={}, attempted={}). Skipping AI analysis.", max, current);
            return false;
        }
        logger.debug("[AiRuntimeBudget] AI call {}/{} acquired.", current, max);
        return true;
    }

    /**
     * Returns the number of AI calls made so far in this run.
     */
    public static int getCallCount() {
        return callCount.get();
    }

    /**
     * Resets the counter — primarily useful for unit tests.
     */
    public static void reset() {
        callCount.set(0);
    }
}
