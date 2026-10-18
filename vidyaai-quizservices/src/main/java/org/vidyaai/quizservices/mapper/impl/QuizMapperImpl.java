package org.vidyaai.quizservices.mapper.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.quizservices.dto.response.QuestionResponse;
import org.vidyaai.quizservices.dto.response.QuizResponse;
import org.vidyaai.quizservices.entity.Quiz;
import org.vidyaai.quizservices.entity.QuizQuestion;
import org.vidyaai.quizservices.mapper.QuizMapper;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuizMapperImpl implements QuizMapper {

    private final ObjectMapper objectMapper;

    @Override
    public QuizResponse toResponse(Quiz entity, List<QuizQuestion> questions) {
        try {
            return QuizResponse.builder()
                    .id(entity.getId())
                    .videoId(entity.getVideoId())
                    .userId(entity.getUserId())
                    .title(entity.getTitle())
                    .description(entity.getDescription())
                    .difficultyLevel(entity.getDifficultyLevel())
                    .totalQuestions(entity.getTotalQuestions())
                    .passingScore(entity.getPassingScore())
                    .timeLimitMinutes(entity.getTimeLimitMinutes())
                    .isPublished(entity.getIsPublished())
                    .questions(questions != null
                            ? questions.stream().map(this::toQuestionResponse).toList()
                            : Collections.emptyList())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping Quiz to QuizResponse: {}", e.getMessage(), e);
            return QuizResponse.builder()
                    .id(entity.getId())
                    .videoId(entity.getVideoId())
                    .title(entity.getTitle())
                    .build();
        }
    }

    @Override
    public QuizResponse toSummaryResponse(Quiz entity) {
        return QuizResponse.builder()
                .id(entity.getId())
                .videoId(entity.getVideoId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .difficultyLevel(entity.getDifficultyLevel())
                .totalQuestions(entity.getTotalQuestions())
                .passingScore(entity.getPassingScore())
                .timeLimitMinutes(entity.getTimeLimitMinutes())
                .isPublished(entity.getIsPublished())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public QuestionResponse toQuestionResponse(QuizQuestion entity) {
        try {
            return QuestionResponse.builder()
                    .id(entity.getId())
                    .questionType(entity.getQuestionType())
                    .questionText(entity.getQuestionText())
                    .options(parseOptions(entity.getOptions()))
                    .marks(entity.getMarks())
                    .questionOrder(entity.getQuestionOrder())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping QuizQuestion to QuestionResponse: {}", e.getMessage(), e);
            return QuestionResponse.builder()
                    .id(entity.getId())
                    .questionText(entity.getQuestionText())
                    .options(Collections.emptyList())
                    .build();
        }
    }

    private List<String> parseOptions(String optionsJson) {
        if (optionsJson == null || optionsJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(optionsJson, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            log.warn("Failed to parse options JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}