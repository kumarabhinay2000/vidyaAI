package org.vidyaai.quizservices.service;

import org.vidyaai.quizservices.dto.request.CreateQuizRequest;
import org.vidyaai.quizservices.dto.request.UpdateQuizRequest;
import org.vidyaai.quizservices.dto.response.QuizResponse;

import java.util.List;
import java.util.UUID;

public interface QuizService {

    QuizResponse createQuiz(CreateQuizRequest request);

    QuizResponse getQuiz(UUID quizId);

    QuizResponse getQuizForTaking(UUID quizId);

    QuizResponse getQuizByVideoId(UUID videoId);

    List<QuizResponse> listUserQuizzes(UUID userId);

    QuizResponse updateQuiz(UUID quizId, UpdateQuizRequest request);

    void deleteQuiz(UUID quizId);
}