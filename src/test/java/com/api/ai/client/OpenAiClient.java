package com.api.ai.client;

import com.api.ai.config.AiConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Minimal OpenAI Chat Completions client using Java 11 {@link HttpClient}.
 *
 * Responsibilities:
 *  - Build a JSON request with system + user messages
 *  - POST to https://api.openai.com/v1/chat/completions
 *  - Extract the assistant's "content" string from the response
 *  - Return null on non-2xx or malformed responses (caller treats as "no AI result")
 *
 * No third-party HTTP library required — uses standard Java 11 APIs.
 */
public class OpenAiClient {

    private static final Logger logger = LogManager.getLogger(OpenAiClient.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private OpenAiClient() {}

    /**
     * Sends a chat completion request to OpenAI.
     *
     * @param systemMessage the system-role message (sets context/format)
     * @param userMessage   the user-role message (the actual prompt)
     * @return the assistant response content string, or {@code null} if the
     *         call fails or returns no usable content
     */
    public static String createChatCompletion(String systemMessage, String userMessage) {
        String apiKey = AiConfig.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("[OpenAiClient] No OPENAI_API_KEY found — skipping AI call.");
            return null;
        }

        try {
            // Build JSON body
            ObjectNode body = MAPPER.createObjectNode();
            body.put("model", AiConfig.getModel());

            ArrayNode messages = body.putArray("messages");

            ObjectNode sysMsg = MAPPER.createObjectNode();
            sysMsg.put("role", "system");
            sysMsg.put("content", systemMessage);
            messages.add(sysMsg);

            ObjectNode userMsg = MAPPER.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            // Temperature 0 for deterministic / structured output
            body.put("temperature", 0);

            String requestBody = MAPPER.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AiConfig.OPENAI_CHAT_URL))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            logger.debug("[OpenAiClient] Sending request to OpenAI (model={})...", AiConfig.getModel());

            HttpResponse<String> response = HTTP_CLIENT.send(request,
                    HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            if (statusCode < 200 || statusCode >= 300) {
                logger.warn("[OpenAiClient] Non-2xx response from OpenAI: {} — body: {}",
                        statusCode, response.body());
                return null;
            }

            JsonNode responseJson = MAPPER.readTree(response.body());
            JsonNode choices = responseJson.path("choices");
            if (choices.isEmpty()) {
                logger.warn("[OpenAiClient] OpenAI returned empty choices array.");
                return null;
            }

            String content = choices.get(0).path("message").path("content").asText(null);
            if (content == null || content.isBlank()) {
                logger.warn("[OpenAiClient] OpenAI returned blank content.");
                return null;
            }

            logger.debug("[OpenAiClient] Received response ({} chars).", content.length());
            return content;

        } catch (Exception e) {
            logger.warn("[OpenAiClient] Exception during OpenAI call: {}", e.getMessage());
            return null;
        }
    }
}
