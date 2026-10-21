package com.api.utility;

import io.restassured.response.Response;
import org.testng.Assert;

/**
 * Utility class providing centralized assertion logic for API responses.
 * Reused by CommonSteps and other step definitions for consistent assertion messages.
 */
public class AssertionUtils {

    private AssertionUtils() {
        // Utility class
    }

    /**
     * Asserts that the response status code matches the expected status code.
     * Allows 201 when 200 is expected to handle API endpoints returning either on creation.
     */
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        Assert.assertNotNull(response, "No response found — was a request step executed?");
        int actualStatusCode = response.getStatusCode();
        boolean matched = (actualStatusCode == expectedStatusCode) ||
                (expectedStatusCode == 200 && actualStatusCode == 201);

        Assert.assertTrue(
                matched,
                String.format("Expected status code %d but got %d\nResponse Body:\n%s",
                        expectedStatusCode, actualStatusCode, response.getBody().asString())
        );
    }

    /**
     * Asserts that the response body contains the specified substring.
     */
    public static void assertBodyContains(Response response, String expectedText) {
        Assert.assertNotNull(response, "No response found to assert body content");
        String body = response.getBody().asString();
        Assert.assertTrue(
                body.contains(expectedText),
                String.format("Expected response body to contain '%s' but got:\n%s", expectedText, body)
        );
    }

    /**
     * Asserts that the response body is not null, empty, or whitespace only.
     */
    public static void assertBodyNotEmpty(Response response) {
        Assert.assertNotNull(response, "No response found to check body");
        String body = response.getBody().asString();
        Assert.assertFalse(
                body == null || body.trim().isEmpty(),
                "Response body is empty but was expected to contain data"
        );
    }

    /**
     * Asserts that a specific field in the response JSON is not null.
     */
    public static void assertFieldNotNull(Response response, String jsonPath) {
        Assert.assertNotNull(response, "No response found to check field");
        Object val = response.jsonPath().get(jsonPath);
        Assert.assertNotNull(val, "Expected JSON field '" + jsonPath + "' to not be null in response: " + response.getBody().asString());
    }
}
