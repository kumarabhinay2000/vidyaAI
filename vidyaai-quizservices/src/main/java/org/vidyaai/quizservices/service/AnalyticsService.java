package org.vidyaai.quizservices.service;

import org.vidyaai.quizservices.dto.response.QuizAnalyticsResponse;

import java.util.UUID;

public interface AnalyticsService {

    QuizAnalyticsResponse getQuizAnalytics(UUID quizId);

    void refreshAnalytics(UUID quizId);

    void deleteAnalytics(UUID quizId);
}