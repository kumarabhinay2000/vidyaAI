package org.vidyaai.quizservices.service;

import org.vidyaai.quizservices.dto.request.StartAttemptRequest;
import org.vidyaai.quizservices.dto.request.SubmitQuizRequest;
import org.vidyaai.quizservices.dto.response.AttemptResultResponse;
import org.vidyaai.quizservices.dto.response.QuizAttemptResponse;

import java.util.List;
import java.util.UUID;

public interface AttemptService {

    QuizAttemptResponse startAttempt(StartAttemptRequest request, UUID userId);

    AttemptResultResponse submitAttempt(SubmitQuizRequest request, UUID userId);

    QuizAttemptResponse getAttempt(UUID attemptId, UUID userId);

    List<QuizAttemptResponse> getUserAttempts(UUID userId);

    List<QuizAttemptResponse> getQuizAttempts(UUID quizId);
}