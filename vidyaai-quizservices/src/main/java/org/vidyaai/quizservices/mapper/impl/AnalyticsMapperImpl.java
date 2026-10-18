package org.vidyaai.quizservices.mapper.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.quizservices.dto.response.QuizAnalyticsResponse;
import org.vidyaai.quizservices.entity.QuizAnalytics;
import org.vidyaai.quizservices.mapper.AnalyticsMapper;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsMapperImpl implements AnalyticsMapper {

    private final ObjectMapper objectMapper;

    @Override
    public QuizAnalyticsResponse toResponse(QuizAnalytics entity) {
        try {
            return QuizAnalyticsResponse.builder()
                    .quizId(entity.getQuizId())
                    .totalAttempts(entity.getTotalAttempts())
                    .avgScore(entity.getAvgScore())
                    .highestScore(entity.getHighestScore())
                    .lowestScore(entity.getLowestScore())
                    .passRate(entity.getPassRate())
                    .avgTimeTaken(entity.getAvgTimeTaken())
                    .difficultQuestions(parseMap(entity.getDifficultQuestions()))
                    .easyQuestions(parseMap(entity.getEasyQuestions()))
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping QuizAnalytics: {}", e.getMessage(), e);
            return QuizAnalyticsResponse.builder()
                    .quizId(entity.getQuizId())
                    .build();
        }
    }

    private Map<UUID, Double> parseMap(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<UUID, Double>>() {
            });
        } catch (Exception e) {
            log.warn("Failed to parse analytics map: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
}