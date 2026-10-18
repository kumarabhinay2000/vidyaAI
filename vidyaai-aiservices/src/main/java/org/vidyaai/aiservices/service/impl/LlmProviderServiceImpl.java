package org.vidyaai.aiservices.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vidyaai.aiservices.client.GroqClient;
import org.vidyaai.aiservices.enums.LlmProvider;
import org.vidyaai.aiservices.exception.LlmProviderException;
import org.vidyaai.aiservices.service.LlmProviderService;

@Service
@RequiredArgsConstructor
@Slf4j
public class LlmProviderServiceImpl implements LlmProviderService {

    private final GroqClient groqClient;

    @Override
    public LlmProvider getActiveProvider() {
        if (groqClient.isAvailable()) {
            return LlmProvider.GROQ;
        }
        throw new LlmProviderException("GROQ", "No active LLM provider available");
    }

    @Override
    public boolean isProviderAvailable(LlmProvider provider) {
        try {
            return provider == LlmProvider.GROQ && groqClient.isAvailable();
        } catch (Exception e) {
            log.warn("Error checking provider availability {}: {}", provider, e.getMessage());
            return false;
        }
    }

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        try {
            return groqClient.chat(systemPrompt, userPrompt);
        } catch (Exception e) {
            log.error("LLM chat failed: {}", e.getMessage(), e);
            throw new LlmProviderException("GROQ", "LLM chat failed", e);
        }
    }

    @Override
    public String chatJson(String systemPrompt, String userPrompt) {
        try {
            return groqClient.chatWithJsonMode(systemPrompt, userPrompt);
        } catch (Exception e) {
            log.error("LLM JSON chat failed: {}", e.getMessage(), e);
            throw new LlmProviderException("GROQ", "LLM JSON chat failed", e);
        }
    }

    @Override
    public float[] embed(String text) {
        try {
            return groqClient.embed(text);
        } catch (Exception e) {
            log.error("LLM embed failed: {}", e.getMessage(), e);
            throw new LlmProviderException("GROQ", "LLM embed failed", e);
        }
    }
}