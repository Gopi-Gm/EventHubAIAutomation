package com.api.steps;

import com.api.context.ScenarioContext;
import com.api.utility.AssertionUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

/**
 * Shared Cucumber assertion steps reused across all feature files.
 *
 * Keeps step definitions clean by delegating assertion logic
 * to {@link com.api.utility.AssertionUtils}.
 */
public class CommonSteps {

    private final ScenarioContext context;

    public CommonSteps(ScenarioContext context) {
        this.context = context;
    }

    // ------------------------------------------------------------------ //
    //  Status code assertions
    // ------------------------------------------------------------------ //

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        AssertionUtils.assertStatusCode(context.getLastResponse(), expectedStatusCode);
    }

    // ------------------------------------------------------------------ //
    //  Body content assertions
    // ------------------------------------------------------------------ //

    @Then("the response body should contain {string}")
    public void theResponseBodyShouldContain(String expectedText) {
        AssertionUtils.assertBodyContains(context.getLastResponse(), expectedText);
    }

    @And("the response body should not be empty")
    public void theResponseBodyShouldNotBeEmpty() {
        AssertionUtils.assertBodyNotEmpty(context.getLastResponse());
    }
}
