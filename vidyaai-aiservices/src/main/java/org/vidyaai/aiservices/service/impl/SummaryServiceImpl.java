package org.vidyaai.aiservices.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.aiservices.constant.CacheKeys;
import org.vidyaai.aiservices.dto.request.SummaryRequest;
import org.vidyaai.aiservices.dto.response.SummaryResponse;
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
import org.vidyaai.aiservices.service.SummaryService;
import org.vidyaai.aiservices.service.TranscriptService;
import org.vidyaai.aiservices.strategy.SummaryStrategy;
import org.vidyaai.aiservices.util.LanguageDetector;
import org.vidyaai.aiservices.validator.SummaryRequestValidator;

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
public class SummaryServiceImpl implements SummaryService {

    private final AiContentRepository aiContentRepository;
    private final AiContentMapper aiContentMapper;
    private final CacheService cacheService;
    private final LlmProviderService llmProviderService;
    private final PromptTemplateService promptTemplateService;
    private final TranscriptService transcriptService;
    private final LanguageDetector languageDetector;
    private final SummaryRequestValidator summaryRequestValidator;
    private final ObjectMapper objectMapper;

    private static final Duration SUMMARY_CACHE_TTL = Duration.ofDays(7);

    @Override
    @Transactional
    public SummaryResponse generateSummary(SummaryRequest request) {
        summaryRequestValidator.validate(request);
        long start = System.currentTimeMillis();

        UUID videoId = request.getVideoId();
        String languageName = languageDetector.getLanguageName(request.getLanguage());
        String cacheKey = CacheKeys.summary(videoId.toString(),
                request.getType().name(), request.getLanguage().name());

        try {
            // 1. Cache lookup
            if (Boolean.TRUE.equals(request.getUseCache()) && !Boolean.TRUE.equals(request.getForceRegenerate())) {
                Optional<SummaryResponse> cached = cacheService.get(cacheKey, SummaryResponse.class);
                if (cached.isPresent()) {
                    SummaryResponse r = cached.get();
                    r.setCached(true);
                    log.info("Summary cache HIT for videoId={} type={} lang={}",
                            videoId, request.getType(), request.getLanguage());
                    return r;
                }
            }

            // 2. Fetch transcript
            String transcript = fetchTranscript(videoId);
            if (transcript == null || transcript.isBlank()) {
                throw new AiServiceException("Transcript not available for videoId: " + videoId);
            }

            // 3. Resolve strategy
            SummaryStrategy strategy = resolveStrategy(request);

            // 4. Build prompt
            Map<String, Object> variables = strategy.buildVariables(transcript, languageName);
            String prompt = promptTemplateService.renderWithSchema(
                    strategy.getPromptName(), variables, strategy.getExpectedSchema());

            // 5. Call LLM
            String llmResponse = llmProviderService.chatJson(
                    "You are an expert educational summarizer. Return only valid JSON.",
                    prompt);

            // 6. Parse and persist
            Map<String, Object> parsed = parseResponse(llmResponse);
            AiContent entity = persistContent(videoId, request, parsed, start);

            // 7. Build response
            SummaryResponse response = aiContentMapper.toSummaryResponse(entity);
            response.setCached(false);

            // 8. Cache it
            cacheService.put(cacheKey, response, SUMMARY_CACHE_TTL);

            log.info("Summary generated for videoId={} in {}ms",
                    videoId, System.currentTimeMillis() - start);
            return response;

        } catch (AiServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed generating summary for videoId={}: {}", videoId, e.getMessage(), e);
            throw new AiServiceException("Failed to generate summary", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SummaryResponse getSummary(UUID videoId) {
        try {
            return aiContentRepository
                    .findFirstByVideoIdAndContentTypeOrderByCreatedAtDesc(videoId, ContentType.SUMMARY)
                    .map(aiContentMapper::toSummaryResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Summary", "videoId", videoId));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching summary for videoId {}: {}", videoId, e.getMessage(), e);
            throw new AiServiceException("Failed to fetch summary", e);
        }
    }

    @Override
    @Transactional
    public void deleteSummary(UUID videoId) {
        try {
            List<AiContent> existing = aiContentRepository
                    .findByVideoIdAndContentType(videoId, ContentType.SUMMARY);
            if (!existing.isEmpty()) {
                aiContentRepository.deleteAll(existing);
            }
            cacheService.deleteByPattern(CacheKeys.summary(videoId.toString(), "*", "*"));
            log.info("Deleted summaries for videoId={}", videoId);
        } catch (Exception e) {
            log.error("Error deleting summary for videoId {}: {}", videoId, e.getMessage(), e);
            throw new AiServiceException("Failed to delete summary", e);
        }
    }

    // ---------- helpers ----------

    private String fetchTranscript(UUID videoId) {
        try {
            if (transcriptService.hasTranscript(videoId)) {
                return transcriptService.getTranscript(videoId).getTranscript();
            }
            log.warn("No transcript available for videoId={}", videoId);
            return null;
        } catch (Exception e) {
            log.error("Error fetching transcript for videoId {}: {}", videoId, e.getMessage());
            return null;
        }
    }

    private SummaryStrategy resolveStrategy(SummaryRequest request) {
        return switch (request.getType()) {
            case FIVE_POINT -> summaryStrategyResolver().resolveSummary(request.getType());
            case TEN_POINT -> summaryStrategyResolver().resolveSummary(request.getType());
            case DETAILED -> summaryStrategyResolver().resolveSummary(request.getType());
        };
    }

    private org.vidyaai.aiservices.strategy.SummaryStrategyResolver summaryStrategyResolver() {
        // Spring will inject strategies via constructor-based resolver; we fetch from context here
        return org.vidyaai.aiservices.util.BeanLocator.getBean(
                org.vidyaai.aiservices.strategy.SummaryStrategyResolver.class);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseResponse(String llmResponse) {
        try {
            return objectMapper.readValue(llmResponse, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.error("Failed to parse LLM summary response: {}", e.getMessage());
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("title", "Summary");
            fallback.put("points", List.of(llmResponse));
            fallback.put("keywords", List.of());
            return fallback;
        }
    }

    private AiContent persistContent(UUID videoId, SummaryRequest request,
                                     Map<String, Object> parsed, long start) {
        String content = String.join("\n",
                (List<String>) parsed.getOrDefault("points", List.of()));
        String metadata = toJson(parsed);

        AiContent entity = AiContent.builder()
                .videoId(videoId)
                .contentType(ContentType.SUMMARY)
                .title((String) parsed.getOrDefault("title", "Summary"))
                .content(content)
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

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Failed to serialize summary metadata: {}", e.getMessage());
            return "{}";
        }
    }
}