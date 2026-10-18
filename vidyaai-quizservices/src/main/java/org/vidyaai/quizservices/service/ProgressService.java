package org.vidyaai.quizservices.service;

import org.vidyaai.quizservices.dto.response.LearningProgressResponse;
import org.vidyaai.quizservices.entity.QuizAttempt;

import java.util.List;
import java.util.UUID;

public interface ProgressService {

    void updateProgress(UUID userId, UUID videoId, UUID quizId, QuizAttempt attempt);

    LearningProgressResponse getProgress(UUID userId, UUID videoId);

    List<LearningProgressResponse> getUserProgress(UUID userId);
}