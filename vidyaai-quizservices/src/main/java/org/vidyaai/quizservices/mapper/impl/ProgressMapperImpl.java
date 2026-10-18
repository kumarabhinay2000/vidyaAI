package org.vidyaai.quizservices.mapper.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.quizservices.dto.response.LearningProgressResponse;
import org.vidyaai.quizservices.entity.LearningProgress;
import org.vidyaai.quizservices.mapper.ProgressMapper;

@Component
@Slf4j
public class ProgressMapperImpl implements ProgressMapper {

    @Override
    public LearningProgressResponse toResponse(LearningProgress entity) {
        return LearningProgressResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .videoId(entity.getVideoId())
                .quizId(entity.getQuizId())
                .status(entity.getStatus())
                .progressPercentage(entity.getProgressPercentage())
                .attemptsCount(entity.getAttemptsCount())
                .bestScore(entity.getBestScore())
                .lastAccessed(entity.getLastAccessed())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}