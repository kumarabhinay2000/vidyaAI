package org.vidyaai.quizservices.mapper.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.quizservices.dto.response.AttemptResultResponse;
import org.vidyaai.quizservices.dto.response.QuizAttemptResponse;
import org.vidyaai.quizservices.entity.QuizAttempt;
import org.vidyaai.quizservices.mapper.AttemptMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttemptMapperImpl implements AttemptMapper {

    private final ObjectMapper objectMapper;

    @Override
    public QuizAttemptResponse toResponse(QuizAttempt entity) {
        try {
            return QuizAttemptResponse.builder()
                    .id(entity.getId())
                    .quizId(entity.getQuizId())
                    .userId(entity.getUserId())
                    .score(entity.getScore())
                    .totalMarks(entity.getTotalMarks())
                    .correctAnswers(entity.getCorrectAnswers())
                    .wrongAnswers(entity.getWrongAnswers())
                    .unanswered(entity.getUnanswered())
                    .percentage(entity.getPercentage())
                    .timeTakenSeconds(entity.getTimeTakenSeconds())
                    .status(entity.getStatus())
                    .isPassed(entity.getIsPassed())
                    .answers(parseAnswers(entity.getAnswers()))
                    .startedAt(entity.getStartedAt())
                    .completedAt(entity.getCompletedAt())
                    .createdAt(entity.getCreatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping QuizAttempt: {}", e.getMessage(), e);
            return QuizAttemptResponse.builder()
                    .id(entity.getId())
                    .quizId(entity.getQuizId())
                    .userId(entity.getUserId())
                    .build();
        }
    }

    @Override
    public AttemptResultResponse toResultResponse(QuizAttempt entity,
                                                  List<AttemptResultResponse.AnswerReview> reviews) {
        return AttemptResultResponse.builder()
                .attemptId(entity.getId())
                .quizId(entity.getQuizId())
                .score(entity.getScore())
                .totalMarks(entity.getTotalMarks())
                .percentage(entity.getPercentage())
                .isPassed(entity.getIsPassed())
                .correctAnswers(entity.getCorrectAnswers())
                .wrongAnswers(entity.getWrongAnswers())
                .unanswered(entity.getUnanswered())
                .reviews(reviews != null ? reviews : Collections.emptyList())
                .build();
    }

    private Map<UUID, String> parseAnswers(String answersJson) {
        if (answersJson == null || answersJson.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(answersJson, new TypeReference<Map<UUID, String>>() {
            });
        } catch (Exception e) {
            log.warn("Failed to parse answers JSON: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
}