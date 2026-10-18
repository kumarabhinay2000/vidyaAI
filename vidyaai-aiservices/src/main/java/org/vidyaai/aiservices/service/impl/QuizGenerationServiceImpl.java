package org.vidyaai.aiservices.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.aiservices.constant.CacheKeys;
import org.vidyaai.aiservices.dto.request.QuizGenerationRequest;
import org.vidyaai.aiservices.dto.response.QuizGenerationResponse;
import org.vidyaai.aiservices.entity.AiContent;
import org.vidyaai.aiservices.enums.ContentType;
import org.vidyaai.aiservices.enums.LlmProvider;
import org.vidyaai.aiservices.exception.AiServiceException;
import org.vidyaai.aiservices.exception.ResourceNotFoundException;
import org.vidyaai.aiservices.mapper.AiContentMapper;
import org.vidyaai.aiservices.repository.AiContentRepository;
import org.vidyaai.aiservices.service.CacheService;
import org.vidyaai.aiservices.service.LlmProviderService;
import org.vidyaai.aiservices.service.PromptTemplateService;
import org.vidyaai.aiservices.service.QuizGenerationService;
import org.vidyaai.aiservices.service.TranscriptService;
import org.vidyaai.aiservices.strategy.QuizStrategy;
import org.vidyaai.aiservices.strategy.SummaryStrategyResolver;
import org.vidyaai.aiservices.util.LanguageDetector;
import org.vidyaai.aiservices.validator.QuizRequestValidator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizGenerationServiceImpl implements QuizGenerationService {

    private final AiContentRepository aiContentRepository;
    private final AiContentMapper aiContentMapper;
    private final CacheService cacheService;
    private final LlmProviderService llmProviderService;
    private final PromptTemplateService promptTemplateService;
    private final TranscriptService transcriptService;
    private final LanguageDetector languageDetector;
    private final QuizRequestValidator quizRequestValidator;
    private final SummaryStrategyResolver strategyResolver;
    private final ObjectMapper objectMapper;

    private static final Duration QUIZ_CACHE_TTL = Duration.ofDays(7);

    @Override
    @Transactional
    public QuizGenerationResponse generateQuiz(QuizGenerationRequest request) {
        quizRequestValidator.validate(request);
        long start = System.currentTimeMillis();
        UUID videoId = request.getVideoId();
        String languageName = languageDetector.getLanguageName(request.getLanguage());
        String cacheKey = CacheKeys.quiz(videoId.toString(),
                request.getDifficulty().name(), request.getQuestionType().name());

        try {
            if (Boolean.TRUE.equals(request.getUseCache()) && !Boolean.TRUE.equals(request.getForceRegenerate())) {
                Optional<QuizGenerationResponse> cached =
                        cacheService.get(cacheKey, QuizGenerationResponse.class);
                if (cached.isPresent()) {
                    QuizGenerationResponse r = cached.get();
                    r.setCached(true);
                    log.info("Quiz cache HIT for videoId={}", videoId);
                    return r;
                }
            }

            String transcript = fetchTranscript(videoId);
            if (transcript == null || transcript.isBlank()) {
                throw new AiServiceException("Transcript not available for videoId: " + videoId);
            }

            QuizStrategy strategy = strategyResolver.resolveQuiz(request.getQuestionType());
            Map<String, Object> variables = strategy.buildVariables(
                    transcript,
                    request.getDifficulty().name(),
                    request.getNumberOfQuestions(),
                    languageName
            );

            String prompt = promptTemplateService.renderWithSchema(
                    strategy.getPromptName(), variables, strategy.getExpectedSchema());

            String llmResponse = llmProviderService.chatJson(
                    "You are an expert quiz creator. Return only valid JSON.",
                    prompt);

            Map<String, Object> parsed = parseResponse(llmResponse);
            AiContent entity = persistQuiz(videoId, request, parsed, start);

            QuizGenerationResponse response = aiContentMapper.toQuizResponse(entity);
            response.setCached(false);
            cacheService.put(cacheKey, response, QUIZ_CACHE_TTL);

            log.info("Quiz generated for videoId={} in {}ms",
                    videoId, System.currentTimeMillis() - start);
            return response;

        } catch (AiServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed generating quiz for videoId={}: {}", videoId, e.getMessage(), e);
            throw new AiServiceException("Failed to generate quiz", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QuizGenerationResponse getQuiz(UUID videoId) {
        try {
            return aiContentRepository
                    .findFirstByVideoIdAndContentTypeOrderByCreatedAtDesc(videoId, ContentType.QUIZ)
                    .map(aiContentMapper::toQuizResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Quiz", "videoId", videoId));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching quiz for videoId {}: {}", videoId, e.getMessage(), e);
            throw new AiServiceException("Failed to fetch quiz", e);
        }
    }

    @Override
    @Transactional
    public void deleteQuiz(UUID videoId) {
        try {
            List<AiContent> existing = aiContentRepository
                    .findByVideoIdAndContentType(videoId, ContentType.QUIZ);
            if (!existing.isEmpty()) {
                aiContentRepository.deleteAll(existing);
            }
            cacheService.deleteByPattern(CacheKeys.quiz(videoId.toString(), "*", "*"));
            log.info("Deleted quiz for videoId={}", videoId);
        } catch (Exception e) {
            log.error("Error deleting quiz for videoId {}: {}", videoId, e.getMessage(), e);
            throw new AiServiceException("Failed to delete quiz", e);
        }
    }

    private String fetchTranscript(UUID videoId) {
        try {
            if (transcriptService.hasTranscript(videoId)) {
                return transcriptService.getTranscript(videoId).getTranscript();
            }
            return null;
        } catch (Exception e) {
            log.error("Error fetching transcript: {}", e.getMessage());
            return null;
        }
    }

    private Map<String, Object> parseResponse(String llmResponse) {
        try {
            return objectMapper.readValue(llmResponse, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.error("Failed to parse quiz response: {}", e.getMessage());
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("title", "Quiz");
            fallback.put("questions", List.of());
            return fallback;
        }
    }

    private AiContent persistQuiz(UUID videoId, QuizGenerationRequest request,
                                  Map<String, Object> parsed, long start) {
        String metadata = toJson(parsed);
        metadata = injectDifficultyAndType(metadata, request);

        AiContent entity = AiContent.builder()
                .videoId(videoId)
                .contentType(ContentType.QUIZ)
                .title((String) parsed.getOrDefault("title", "Quiz"))
                .content(metadata)
                .metadata(metadata)
                .llmProvider(LlmProvider.GROQ)
                .modelName("groq")
                .language(request.getLanguage().name())
                .processingTimeMs(System.currentTimeMillis() - start)
                .isCached(false)
                .cacheHit(false)
                .createdAt(LocalDateTime.now())
                .build();
        return aiContentRepository.save(entity);
    }

    @SuppressWarnings("unchecked")
    private String injectDifficultyAndType(String metadataJson, QuizGenerationRequest request) {
        try {
            Map<String, Object> map = objectMapper.readValue(metadataJson,
                    new TypeReference<Map<String, Object>>() {
                    });
            map.put("difficulty", request.getDifficulty().name());
            map.put("questionType", request.getQuestionType().name());
            map.put("numberOfQuestions", request.getNumberOfQuestions());
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.warn("Failed to inject quiz metadata: {}", e.getMessage());
            return metadataJson;
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}