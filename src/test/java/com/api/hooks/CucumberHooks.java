package com.api.hooks;

import com.api.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Cucumber lifecycle hooks.
 *
 * PicoContainer injects the shared ScenarioContext so hooks can
 * inspect or reset state if needed.
 */
public class CucumberHooks {

    private static final Logger logger = LogManager.getLogger(CucumberHooks.class);

    private final ScenarioContext context;

    public CucumberHooks(ScenarioContext context) {
        this.context = context;
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        logger.info("▶ Starting scenario: [{}] {}", scenario.getId(), scenario.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            logger.error("✗ Scenario FAILED: {}", scenario.getName());
            if (context.getLastResponse() != null) {
                logger.error("  Last response status : {}", context.getLastResponse().getStatusCode());
                logger.error("  Last response body   : {}", context.getLastResponse().getBody().asString());
            }
        } else {
            logger.info("✔ Scenario PASSED: {}", scenario.getName());
        }
    }
}
