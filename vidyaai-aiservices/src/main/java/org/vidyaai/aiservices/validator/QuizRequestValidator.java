package org.vidyaai.aiservices.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.dto.request.QuizGenerationRequest;
import org.vidyaai.aiservices.exception.InvalidRequestException;

@Component
@Slf4j
public class QuizRequestValidator {

    private static final int MIN_QUESTIONS = 3;
    private static final int MAX_QUESTIONS = 20;

    public void validate(QuizGenerationRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Quiz request cannot be null");
        }
        if (request.getVideoId() == null) {
            throw new InvalidRequestException("Video ID is required");
        }
        if (request.getQuestionType() == null) {
            throw new InvalidRequestException("Question type is required");
        }
        if (request.getNumberOfQuestions() == null) {
            throw new InvalidRequestException("Number of questions is required");
        }
        if (request.getNumberOfQuestions() < MIN_QUESTIONS
                || request.getNumberOfQuestions() > MAX_QUESTIONS) {
            throw new InvalidRequestException(
                    "Number of questions must be between " + MIN_QUESTIONS + " and " + MAX_QUESTIONS);
        }
        log.debug("QuizGenerationRequest validated for video: {}", request.getVideoId());
    }
}