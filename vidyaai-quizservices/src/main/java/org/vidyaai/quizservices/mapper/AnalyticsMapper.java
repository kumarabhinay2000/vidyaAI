package org.vidyaai.quizservices.mapper;

import org.vidyaai.quizservices.dto.response.QuizAnalyticsResponse;
import org.vidyaai.quizservices.entity.QuizAnalytics;

public interface AnalyticsMapper {

    QuizAnalyticsResponse toResponse(QuizAnalytics entity);
}