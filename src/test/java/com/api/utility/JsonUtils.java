package com.api.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.testng.Assert;

/**
 * Utility class for JSON parsing and value extraction using Jackson ObjectMapper.
 * Thread-safe and robust against dynamic JSON structures.
 */
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtils() {
        // Utility class
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * Parse JSON string to JsonNode tree.
     */
    public static JsonNode parse(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            Assert.fail("Failed to parse JSON string: " + e.getMessage() + "\nRaw Content: " + json);
            return null;
        }
    }

    /**
     * Extracts auth JWT token from a response payload.
     * Checks "token", "accessToken", and nested "data.token" / "data.accessToken".
     */
    public static String extractToken(Response response) {
        Assert.assertNotNull(response, "Response cannot be null when extracting token");
        JsonNode root = parse(response.getBody().asString());
        if (root == null) {
            Assert.fail("Empty or null response body when extracting token");
        }

        if (root.hasNonNull("token")) {
            return root.get("token").asText();
        }
        if (root.hasNonNull("accessToken")) {
            return root.get("accessToken").asText();
        }
        if (root.has("data")) {
            JsonNode data = root.get("data");
            if (data.hasNonNull("token")) {
                return data.get("token").asText();
            }
            if (data.hasNonNull("accessToken")) {
                return data.get("accessToken").asText();
            }
        }
        Assert.fail("JWT token not found in response. Body:\n" + response.getBody().asString());
        return null;
    }

    /**
     * Extracts an ID (e.g. event ID, booking ID) from a response payload.
     * Checks "id", "_id", or nested "data.id", "data._id".
     */
    public static String extractId(Response response) {
        Assert.assertNotNull(response, "Response cannot be null when extracting ID");
        JsonNode root = parse(response.getBody().asString());
        if (root == null) {
            Assert.fail("Empty or null response body when extracting ID");
        }

        if (root.hasNonNull("id")) {
            return root.get("id").asText();
        }
        if (root.hasNonNull("_id")) {
            return root.get("_id").asText();
        }
        if (root.has("data")) {
            JsonNode data = root.get("data");
            if (data.hasNonNull("id")) {
                return data.get("id").asText();
            }
            if (data.hasNonNull("_id")) {
                return data.get("_id").asText();
            }
        }
        // Fallback to RestAssured JsonPath if root node didn't match top-level
        String id = response.jsonPath().getString("id");
        if (id == null) {
            id = response.jsonPath().getString("data.id");
        }
        return id;
    }

    /**
     * Converts any Java object to its JSON string representation.
     */
    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON: " + e.getMessage(), e);
        }
    }
}
