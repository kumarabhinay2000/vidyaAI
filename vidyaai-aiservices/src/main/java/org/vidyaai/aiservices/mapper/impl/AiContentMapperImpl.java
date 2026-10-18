package org.vidyaai.aiservices.mapper.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.dto.response.NotesResponse;
import org.vidyaai.aiservices.dto.response.QuizGenerationResponse;
import org.vidyaai.aiservices.dto.response.SummaryResponse;
import org.vidyaai.aiservices.entity.AiContent;
import org.vidyaai.aiservices.enums.QuizDifficulty;
import org.vidyaai.aiservices.enums.QuizQuestionType;
import org.vidyaai.aiservices.enums.SummaryType;
import org.vidyaai.aiservices.mapper.AiContentMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiContentMapperImpl implements AiContentMapper {

    private final ObjectMapper objectMapper;

    @Override
    public SummaryResponse toSummaryResponse(AiContent entity) {
        try {
            Map<String, Object> metadata = parseMetadata(entity.getMetadata());

            List<String> points = getList(metadata, "points");
            List<String> keywords = getList(metadata, "keywords");
            SummaryType type = getEnum(metadata, "type", SummaryType.class, SummaryType.FIVE_POINT);

            return SummaryResponse.builder()
                    .id(entity.getId())
                    .videoId(entity.getVideoId())
                    .title(entity.getTitle())
                    .points(points)
                    .fullContent(entity.getContent())
                    .keywords(keywords)
                    .type(type)
                    .language(org.vidyaai.aiservices.enums.Language.valueOf(
                            entity.getLanguage() != null ? entity.getLanguage().toUpperCase() : "ENGLISH"))
                    .llmProvider(entity.getLlmProvider())
                    .modelName(entity.getModelName())
                    .tokensUsed(entity.getTokensUsed())
                    .processingTimeMs(entity.getProcessingTimeMs())
                    .cached(entity.getCacheHit())
                    .createdAt(entity.getCreatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping AiContent to SummaryResponse: {}", e.getMessage(), e);
            return SummaryResponse.builder()
                    .id(entity.getId())
                    .videoId(entity.getVideoId())
                    .title(entity.getTitle())
                    .fullContent(entity.getContent())
                    .build();
        }
    }

    @Override
    public QuizGenerationResponse toQuizResponse(AiContent entity) {
        try {
            Map<String, Object> metadata = parseMetadata(entity.getMetadata());

            List<Map<String, Object>> questionsRaw = getListOfMaps(metadata, "questions");
            List<QuizGenerationResponse.QuizQuestion> questions = questionsRaw.stream()
                    .map(this::mapQuestion)
                    .toList();

            QuizDifficulty difficulty = getEnum(metadata, "difficulty",
                    QuizDifficulty.class, QuizDifficulty.MEDIUM);
            QuizQuestionType questionType = getEnum(metadata, "questionType",
                    QuizQuestionType.class, QuizQuestionType.MCQ);

            return QuizGenerationResponse.builder()
                    .id(entity.getId())
                    .videoId(entity.getVideoId())
                    .title(entity.getTitle())
                    .questions(questions)
                    .difficulty(difficulty)
                    .questionType(questionType)
                    .language(org.vidyaai.aiservices.enums.Language.valueOf(
                            entity.getLanguage() != null ? entity.getLanguage().toUpperCase() : "ENGLISH"))
                    .llmProvider(entity.getLlmProvider())
                    .modelName(entity.getModelName())
                    .totalQuestions(questions.size())
                    .tokensUsed(entity.getTokensUsed())
                    .processingTimeMs(entity.getProcessingTimeMs())
                    .cached(entity.getCacheHit())
                    .createdAt(entity.getCreatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping AiContent to QuizGenerationResponse: {}", e.getMessage(), e);
            return QuizGenerationResponse.builder()
                    .id(entity.getId())
                    .videoId(entity.getVideoId())
                    .title(entity.getTitle())
                    .questions(Collections.emptyList())
                    .build();
        }
    }

    @Override
    public NotesResponse toNotesResponse(AiContent entity) {
        try {
            Map<String, Object> metadata = parseMetadata(entity.getMetadata());

            List<Map<String, Object>> sectionsRaw = getListOfMaps(metadata, "sections");
            List<NotesResponse.NoteSection> sections = sectionsRaw.stream()
                    .map(this::mapSection)
                    .toList();

            return NotesResponse.builder()
                    .id(entity.getId())
                    .videoId(entity.getVideoId())
                    .title(entity.getTitle())
                    .sections(sections)
                    .fullContent(entity.getContent())
                    .language(org.vidyaai.aiservices.enums.Language.valueOf(
                            entity.getLanguage() != null ? entity.getLanguage().toUpperCase() : "ENGLISH"))
                    .llmProvider(entity.getLlmProvider())
                    .modelName(entity.getModelName())
                    .tokensUsed(entity.getTokensUsed())
                    .processingTimeMs(entity.getProcessingTimeMs())
                    .cached(entity.getCacheHit())
                    .createdAt(entity.getCreatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping AiContent to NotesResponse: {}", e.getMessage(), e);
            return NotesResponse.builder()
                    .id(entity.getId())
                    .videoId(entity.getVideoId())
                    .title(entity.getTitle())
                    .fullContent(entity.getContent())
                    .sections(Collections.emptyList())
                    .build();
        }
    }

    // -------- helpers --------

    private Map<String, Object> parseMetadata(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            log.warn("Failed to parse metadata JSON: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> getList(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof List<?> list) {
            return (List<String>) list;
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getListOfMaps(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return Collections.emptyList();
    }

    private <E extends Enum<E>> E getEnum(Map<String, Object> map, String key,
                                          Class<E> enumClass, E defaultValue) {
        Object val = map.get(key);
        if (val == null) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(enumClass, val.toString().toUpperCase());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private QuizGenerationResponse.QuizQuestion mapQuestion(Map<String, Object> raw) {
        return QuizGenerationResponse.QuizQuestion.builder()
                .index(getInt(raw, "index"))
                .questionText(getString(raw, "question"))
                .options(getList(raw, "options"))
                .correctAnswer(getString(raw, "correctAnswer"))
                .explanation(getString(raw, "explanation"))
                .marks(getInt(raw, "marks") != null ? getInt(raw, "marks") : 1)
                .build();
    }

    private NotesResponse.NoteSection mapSection(Map<String, Object> raw) {
        return NotesResponse.NoteSection.builder()
                .heading(getString(raw, "heading"))
                .bulletPoints(getList(raw, "bulletPoints"))
                .startTimeSeconds(getInt(raw, "startTimeSeconds"))
                .build();
    }

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }

    private Integer getInt(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number n) {
            return n.intValue();
        }
        if (val instanceof String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}