package org.vidyaai.quizservices.mapper;

import org.vidyaai.quizservices.dto.response.AttemptResultResponse;
import org.vidyaai.quizservices.dto.response.QuizAttemptResponse;
import org.vidyaai.quizservices.entity.QuizAttempt;

public interface AttemptMapper {

    QuizAttemptResponse toResponse(QuizAttempt entity);

    AttemptResultResponse toResultResponse(QuizAttempt entity,
                                           java.util.List<AttemptResultResponse.AnswerReview> reviews);
}