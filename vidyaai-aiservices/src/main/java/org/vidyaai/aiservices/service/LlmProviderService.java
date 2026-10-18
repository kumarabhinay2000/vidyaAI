package org.vidyaai.aiservices.service;

import org.vidyaai.aiservices.enums.LlmProvider;

public interface LlmProviderService {

    LlmProvider getActiveProvider();

    boolean isProviderAvailable(LlmProvider provider);

    String chat(String systemPrompt, String userPrompt);

    String chatJson(String systemPrompt, String userPrompt);

    float[] embed(String text);
}