package org.vidyaai.aiservices.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.client.GroqClient;
import org.vidyaai.aiservices.config.GroqConfig;
import org.vidyaai.aiservices.dto.response.LlmUsageResponse;
import org.vidyaai.aiservices.entity.LlmUsageLog;
import org.vidyaai.aiservices.enums.LlmProvider;
import org.vidyaai.aiservices.exception.LlmProviderException;
import org.vidyaai.aiservices.exception.LlmTimeoutException;
import org.vidyaai.aiservices.factory.LlmProviderFactory;
import org.vidyaai.aiservices.mapper.LlmUsageMapper;
import org.vidyaai.aiservices.repository.LlmUsageLogRepository;
import org.vidyaai.aiservices.util.JsonUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroqClientImpl implements GroqClient {

    private final LlmProviderFactory llmProviderFactory;
    private final GroqConfig groqConfig;
    private final EmbeddingModel embeddingModel;
    private final LlmUsageLogRepository llmUsageLogRepository;
    private final LlmUsageMapper llmUsageMapper;
    private final JsonUtil jsonUtil;

    @Override
    @CircuitBreaker(name = "groqChat", fallbackMethod = "chatFallback")
    @Retry(name = "groqChatRetry")
    public String chat(String systemPrompt, String userPrompt) {
        long start = System.currentTimeMillis();
        LlmUsageLog usageLog = null;
        try {
            if (systemPrompt == null || userPrompt == null) {
                throw new LlmProviderException("GROQ", "System/user prompt cannot be null");
            }

            ChatClient client = llmProviderFactory.getDefaultClient();
            ChatResponse response = client.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .chatResponse();

            if (response == null || response.getResult() == null
                    || response.getResult().getOutput() == null) {
                throw new LlmProviderException("GROQ", "Empty response from Groq");
            }

            String content = response.getResult().getOutput().getText();
            Usage usage = response.getMetadata() != null ? response.getMetadata().getUsage() : null;

            Integer inputTokens = usage != null ? usage.getPromptTokens() : null;
            Integer outputTokens = usage != null ? usage.getCompletionTokens() : null;

            long elapsed = System.currentTimeMillis() - start;
            usageLog = llmUsageMapper.toEntity(
                    "ai-service", "chat", "groq",
                    groqConfig.getModel(), inputTokens, outputTokens,
                    elapsed, true, null);
            llmUsageLogRepository.save(usageLog);

            log.info("Groq chat completed in {}ms (in={}, out={} tokens)",
                    elapsed, inputTokens, outputTokens);
            return content;
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("Groq chat failed after {}ms: {}", elapsed, e.getMessage(), e);
            try {
                LlmUsageLog failed = llmUsageMapper.toEntity(
                        "ai-service", "chat", "groq",
                        groqConfig.getModel(), null, null,
                        elapsed, false, e.getMessage());
                llmUsageLogRepository.save(failed);
            } catch (Exception ignored) {
                log.warn("Failed to persist failed LLM usage log");
            }
            throw new LlmProviderException("GROQ", "Groq chat call failed: " + e.getMessage(), e);
        }
    }

    @Override
    @CircuitBreaker(name = "groqChat", fallbackMethod = "chatJsonFallback")
    @Retry(name = "groqChatRetry")
    public String chatWithJsonMode(String systemPrompt, String userPrompt) {
        try {
            String raw = chat(systemPrompt, userPrompt);
            String cleaned = jsonUtil.cleanJsonResponse(raw);
            if (!jsonUtil.isValidJson(cleaned)) {
                log.warn("Groq response is not valid JSON; returning cleaned text");
            }
            return cleaned;
        } catch (LlmProviderException e) {
            throw e;
        } catch (Exception e) {
            log.error("Groq JSON chat failed: {}", e.getMessage(), e);
            throw new LlmProviderException("GROQ", "Groq JSON chat failed", e);
        }
    }

    @Override
    @CircuitBreaker(name = "groqEmbedding", fallbackMethod = "embedFallback")
    public float[] embed(String text) {
        long start = System.currentTimeMillis();
        try {
            if (text == null || text.isBlank()) {
                throw new LlmProviderException("GROQ", "Text to embed cannot be blank");
            }
            EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));
            if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                throw new LlmProviderException("GROQ", "Empty embedding response");
            }
            float[] embedding = response.getResults().get(0).getOutput();
            log.debug("Embedding generated in {}ms, dims={}",
                    System.currentTimeMillis() - start, embedding.length);
            return embedding;
        } catch (Exception e) {
            log.error("Embedding failed: {}", e.getMessage(), e);
            throw new LlmProviderException("GROQ", "Embedding failed", e);
        }
    }

    @Override
    public LlmUsageResponse getUsageStats(int days) {
        try {
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusDays(days);
            List<LlmUsageLog> logs = llmUsageLogRepository
                    .findByLlmProviderAndCreatedAtBetween(LlmProvider.GROQ, start, end);
            return llmUsageMapper.toResponse(start, end, logs);
        } catch (Exception e) {
            log.error("Failed to get usage stats: {}", e.getMessage(), e);
            return llmUsageMapper.toResponse(LocalDateTime.now().minusDays(days),
                    LocalDateTime.now(), Collections.emptyList());
        }
    }

    @Override
    public boolean isAvailable() {
        return llmProviderFactory.isAvailable(LlmProvider.GROQ);
    }

    // ---------- fallbacks ----------

    public String chatFallback(String systemPrompt, String userPrompt, Throwable t) {
        log.error("Circuit open: Groq chat fallback triggered: {}", t.getMessage());
        throw new LlmTimeoutException("Groq chat unavailable, circuit open",
                TimeUnit.SECONDS.toMillis(groqConfig.getTimeoutSeconds() != null
                        ? groqConfig.getTimeoutSeconds() : 30L), t);
    }

    public String chatJsonFallback(String systemPrompt, String userPrompt, Throwable t) {
        log.error("Circuit open: Groq JSON chat fallback: {}", t.getMessage());
        throw new LlmProviderException("GROQ", "Groq JSON chat unavailable", t);
    }

    public float[] embedFallback(String text, Throwable t) {
        log.error("Circuit open: Groq embed fallback: {}", t.getMessage());
        throw new LlmProviderException("GROQ", "Embedding unavailable", t);
    }

    @SuppressWarnings("unused")
    private JsonNode unusedAnchor() {
        return null;
    }
}