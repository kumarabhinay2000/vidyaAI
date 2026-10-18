package org.vidyaai.aiservices.client;

import org.vidyaai.aiservices.dto.response.LlmUsageResponse;

public interface GroqClient {

    String chat(String systemPrompt, String userPrompt);

    String chatWithJsonMode(String systemPrompt, String userPrompt);

    float[] embed(String text);

    LlmUsageResponse getUsageStats(int days);

    boolean isAvailable();
}