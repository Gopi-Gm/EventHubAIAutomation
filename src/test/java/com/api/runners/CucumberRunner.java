package com.api.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Cucumber TestNG Runner.
 *
 * Extending {@link AbstractTestNGCucumberTests} makes Cucumber fire standard
 * TestNG lifecycle events — so the existing {@code AiReportEnhancer} listener
 * registered in suite.xml continues to work without any modifications.
 *
 * Run all features:
 *   mvn test -Dsuite=suite
 *
 * Run a specific tag:
 *   mvn test -Dsuite=suite -Dcucumber.filter.tags="@auth"
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue     = { "com.api.steps", "com.api.hooks" },
        plugin   = {
                "pretty",
                "summary",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
                "html:target/cucumber-reports/report.html",
                "json:target/cucumber-reports/report.json",
                "junit:target/cucumber-reports/report.xml"
        },
        monochrome = true
)
public class CucumberRunner extends AbstractTestNGCucumberTests {

    /**
     * Enables parallel scenario execution when thread-count > 1 in suite.xml.
     * Each scenario row is run as an independent TestNG data-provider entry.
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
