package org.vidyaai.aiservices.service;

import org.vidyaai.aiservices.dto.request.QuizGenerationRequest;
import org.vidyaai.aiservices.dto.response.QuizGenerationResponse;

import java.util.UUID;

public interface QuizGenerationService {

    QuizGenerationResponse generateQuiz(QuizGenerationRequest request);

    QuizGenerationResponse getQuiz(UUID videoId);

    void deleteQuiz(UUID videoId);
}