package org.vidyaai.aiservices.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.aiservices.constant.CacheKeys;
import org.vidyaai.aiservices.dto.request.ChatbotRequest;
import org.vidyaai.aiservices.dto.response.ChatbotResponse;
import org.vidyaai.aiservices.entity.AiChatHistory;
import org.vidyaai.aiservices.enums.LlmProvider;
import org.vidyaai.aiservices.exception.AiServiceException;
import org.vidyaai.aiservices.repository.AiChatHistoryRepository;
import org.vidyaai.aiservices.service.CacheService;
import org.vidyaai.aiservices.service.ChatbotService;
import org.vidyaai.aiservices.service.LlmProviderService;
import org.vidyaai.aiservices.service.PromptTemplateService;
import org.vidyaai.aiservices.service.VectorStoreService;
import org.vidyaai.aiservices.util.HashUtil;
import org.vidyaai.aiservices.util.LanguageDetector;
import org.vidyaai.aiservices.validator.ChatbotRequestValidator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {

    private static final String CHATBOT_PROMPT = "chatbot_rag";
    private static final int TOP_K_CHUNKS = 5;
    private static final Duration CHATBOT_CACHE_TTL = Duration.ofHours(24);

    private final AiChatHistoryRepository chatHistoryRepository;
    private final CacheService cacheService;
    private final LlmProviderService llmProviderService;
    private final PromptTemplateService promptTemplateService;
    private final VectorStoreService vectorStoreService;
    private final LanguageDetector languageDetector;
    private final HashUtil hashUtil;
    private final ChatbotRequestValidator chatbotRequestValidator;

    @Override
    @Transactional
    public ChatbotResponse ask(ChatbotRequest request, UUID userId) {
        chatbotRequestValidator.validate(request);
        long start = System.currentTimeMillis();

        UUID videoId = request.getVideoId();
        String sessionId = request.getSessionId() != null
                ? request.getSessionId()
                : UUID.randomUUID().toString();

        String questionHash = hashUtil.shortHash(
                request.getQuestion() + "|" + videoId + "|" + userId);
        String cacheKey = CacheKeys.chatbot(videoId.toString(), userId.toString(), questionHash);

        try {
            // 1. Cache lookup
            Optional<ChatbotResponse> cached = cacheService.get(cacheKey, ChatbotResponse.class);
            if (cached.isPresent()) {
                ChatbotResponse r = cached.get();
                r.setCached(true);
                log.info("Chatbot cache HIT for user={} video={}", userId, videoId);
                return r;
            }

            // 2. RAG: retrieve top-K similar chunks
            List<String> contextChunks = vectorStoreService.findSimilarChunks(
                    videoId, request.getQuestion(), TOP_K_CHUNKS);

            String context = contextChunks.isEmpty()
                    ? "No specific context available."
                    : String.join("\n---\n", contextChunks);

            // 3. Build chat history
            String chatHistory = buildChatHistory(request.getChatHistory());

            // 4. Render prompt
            Map<String, Object> variables = new HashMap<>();
            variables.put("context", context);
            variables.put("question", request.getQuestion());
            variables.put("chatHistory", chatHistory);
            variables.put("language", languageDetector.getLanguageName(request.getLanguage()));

            String prompt = promptTemplateService.render(CHATBOT_PROMPT, variables);

            // 5. LLM call
            String answer = llmProviderService.chat(
                    "You are VidyaAI Tutor. Answer only from the provided context. "
                            + "If not in context, say you don't know. Be helpful and concise.",
                    prompt);

            long elapsed = System.currentTimeMillis() - start;

            // 6. Persist chat history
            AiChatHistory history = AiChatHistory.builder()
                    .videoId(videoId)
                    .userId(userId)
                    .sessionId(sessionId)
                    .question(request.getQuestion())
                    .answer(answer)
                    .contextUsed(context)
                    .responseTimeMs(elapsed)
                    .build();
            AiChatHistory saved = chatHistoryRepository.save(history);

            // 7. Build response
            ChatbotResponse response = ChatbotResponse.builder()
                    .chatId(saved.getId())
                    .videoId(videoId)
                    .sessionId(sessionId)
                    .answer(answer)
                    .sources(contextChunks)
                    .confidence(computeConfidence(contextChunks.size()))
                    .llmProvider(LlmProvider.GROQ)
                    .modelName("groq")
                    .responseTimeMs(elapsed)
                    .cached(false)
                    .createdAt(saved.getCreatedAt())
                    .build();

            // 8. Cache
            cacheService.put(cacheKey, response, CHATBOT_CACHE_TTL);

            log.info("Chatbot answer produced in {}ms for user={} video={}",
                    elapsed, userId, videoId);
            return response;

        } catch (AiServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Chatbot error for user={} video={}: {}", userId, videoId, e.getMessage(), e);
            throw new AiServiceException("Failed to answer chatbot query", e);
        }
    }

    @Override
    @Transactional
    public void deleteChatHistory(UUID userId, UUID videoId) {
        try {
            List<AiChatHistory> items = chatHistoryRepository
                    .findByUserIdAndVideoIdOrderByCreatedAtAsc(userId, videoId);
            if (!items.isEmpty()) {
                chatHistoryRepository.deleteAll(items);
            }
            log.info("Deleted {} chat history entries for user={} video={}",
                    items.size(), userId, videoId);
        } catch (Exception e) {
            log.error("Error deleting chat history: {}", e.getMessage(), e);
            throw new AiServiceException("Failed to delete chat history", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countUserMessages(UUID userId) {
        try {
            return chatHistoryRepository.countByUserId(userId);
        } catch (Exception e) {
            log.error("Error counting user messages: {}", e.getMessage());
            return 0L;
        }
    }

    private String buildChatHistory(List<ChatbotRequest.ChatMessage> history) {
        if (history == null || history.isEmpty()) {
            return "";
        }
        return history.stream()
                .map(m -> m.getRole() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));
    }

    private Double computeConfidence(int contextCount) {
        if (contextCount <= 0) {
            return 0.3;
        }
        return Math.min(0.95, 0.5 + (contextCount * 0.09));
    }

    @SuppressWarnings("unused")
    private List<String> emptySources() {
        return Collections.emptyList();
    }
}