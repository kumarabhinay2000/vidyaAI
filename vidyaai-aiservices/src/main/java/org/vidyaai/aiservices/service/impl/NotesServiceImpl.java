package org.vidyaai.aiservices.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.aiservices.constant.CacheKeys;
import org.vidyaai.aiservices.dto.request.NotesRequest;
import org.vidyaai.aiservices.dto.response.NotesResponse;
import org.vidyaai.aiservices.entity.AiContent;
import org.vidyaai.aiservices.enums.ContentType;
import org.vidyaai.aiservices.enums.LlmProvider;
import org.vidyaai.aiservices.exception.AiServiceException;
import org.vidyaai.aiservices.exception.ResourceNotFoundException;
import org.vidyaai.aiservices.mapper.AiContentMapper;
import org.vidyaai.aiservices.repository.AiContentRepository;
import org.vidyaai.aiservices.service.CacheService;
import org.vidyaai.aiservices.service.LlmProviderService;
import org.vidyaai.aiservices.service.NotesService;
import org.vidyaai.aiservices.service.PromptTemplateService;
import org.vidyaai.aiservices.service.TranscriptService;
import org.vidyaai.aiservices.util.LanguageDetector;

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
public class NotesServiceImpl implements NotesService {

    private final AiContentRepository aiContentRepository;
    private final AiContentMapper aiContentMapper;
    private final CacheService cacheService;
    private final LlmProviderService llmProviderService;
    private final PromptTemplateService promptTemplateService;
    private final TranscriptService transcriptService;
    private final LanguageDetector languageDetector;
    private final ObjectMapper objectMapper;

    private static final Duration NOTES_CACHE_TTL = Duration.ofDays(7);
    private static final String NOTES_PROMPT = "notes_bullet";

    private static final String EXPECTED_SCHEMA = """
            {
              "title": "string",
              "sections": [
                {
                  "heading": "string",
                  "bulletPoints": ["string"],
                  "startTimeSeconds": 0
                }
              ]
            }
            """;

    @Override
    @Transactional
    public NotesResponse generateNotes(NotesRequest request) {
        long start = System.currentTimeMillis();
        UUID videoId = request.getVideoId();
        String languageName = languageDetector.getLanguageName(request.getLanguage());
        String cacheKey = CacheKeys.notes(videoId.toString(), request.getLanguage().name());

        try {
            if (Boolean.TRUE.equals(request.getUseCache()) && !Boolean.TRUE.equals(request.getForceRegenerate())) {
                Optional<NotesResponse> cached = cacheService.get(cacheKey, NotesResponse.class);
                if (cached.isPresent()) {
                    NotesResponse r = cached.get();
                    r.setCached(true);
                    log.info("Notes cache HIT for videoId={}", videoId);
                    return r;
                }
            }

            String transcript = fetchTranscript(videoId);
            if (transcript == null || transcript.isBlank()) {
                throw new AiServiceException("Transcript not available for videoId: " + videoId);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("content", transcript);
            variables.put("language", languageName);
            variables.put("includeTimestamps", Boolean.TRUE.equals(request.getIncludeTimestamps()));

            String prompt = promptTemplateService.renderWithSchema(
                    NOTES_PROMPT, variables, EXPECTED_SCHEMA);

            String response = llmProviderService.chatJson(
                    "You are an expert note-maker. Return only valid JSON.",
                    prompt);

            Map<String, Object> parsed = parseResponse(response);
            AiContent entity = persistNotes(videoId, request, parsed, start);

            NotesResponse notesResponse = aiContentMapper.toNotesResponse(entity);
            notesResponse.setCached(false);
            cacheService.put(cacheKey, notesResponse, NOTES_CACHE_TTL);

            log.info("Notes generated for videoId={} in {}ms",
                    videoId, System.currentTimeMillis() - start);
            return notesResponse;

        } catch (AiServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed generating notes for videoId={}: {}", videoId, e.getMessage(), e);
            throw new AiServiceException("Failed to generate notes", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public NotesResponse getNotes(UUID videoId) {
        try {
            return aiContentRepository
                    .findFirstByVideoIdAndContentTypeOrderByCreatedAtDesc(videoId, ContentType.NOTES)
                    .map(aiContentMapper::toNotesResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Notes", "videoId", videoId));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching notes for videoId {}: {}", videoId, e.getMessage(), e);
            throw new AiServiceException("Failed to fetch notes", e);
        }
    }

    @Override
    @Transactional
    public void deleteNotes(UUID videoId) {
        try {
            List<AiContent> existing = aiContentRepository
                    .findByVideoIdAndContentType(videoId, ContentType.NOTES);
            if (!existing.isEmpty()) {
                aiContentRepository.deleteAll(existing);
            }
            cacheService.deleteByPattern(CacheKeys.notes(videoId.toString(), "*"));
            log.info("Deleted notes for videoId={}", videoId);
        } catch (Exception e) {
            log.error("Error deleting notes for videoId {}: {}", videoId, e.getMessage(), e);
            throw new AiServiceException("Failed to delete notes", e);
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
            log.error("Failed to parse notes response: {}", e.getMessage());
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("title", "Notes");
            fallback.put("sections", List.of());
            return fallback;
        }
    }

    private AiContent persistNotes(UUID videoId, NotesRequest request,
                                   Map<String, Object> parsed, long start) {
        String content = toJson(parsed);
        String metadata = toJson(parsed);

        AiContent entity = AiContent.builder()
                .videoId(videoId)
                .contentType(ContentType.NOTES)
                .title((String) parsed.getOrDefault("title", "Notes"))
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
            return "{}";
        }
    }
}