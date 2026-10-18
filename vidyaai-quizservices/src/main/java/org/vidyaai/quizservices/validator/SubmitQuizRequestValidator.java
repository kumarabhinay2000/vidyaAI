package org.vidyaai.quizservices.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.quizservices.dto.request.SubmitQuizRequest;
import org.vidyaai.quizservices.exception.InvalidRequestException;

@Component
@Slf4j
public class SubmitQuizRequestValidator {

    public void validate(SubmitQuizRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Submit request cannot be null");
        }
        if (request.getQuizId() == null) {
            throw new InvalidRequestException("Quiz ID is required");
        }
        if (request.getAttemptId() == null) {
            throw new InvalidRequestException("Attempt ID is required");
        }
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new InvalidRequestException("Answers are required");
        }
        log.debug("SubmitQuizRequest validated for quizId={} attemptId={}",
                request.getQuizId(), request.getAttemptId());
    }
}