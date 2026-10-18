package org.vidyaai.quizservices.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.quizservices.dto.request.CreateQuizRequest;
import org.vidyaai.quizservices.exception.InvalidRequestException;

@Component
@Slf4j
public class CreateQuizRequestValidator {

    public void validate(CreateQuizRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Create quiz request cannot be null");
        }
        if (request.getVideoId() == null) {
            throw new InvalidRequestException("Video ID is required");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new InvalidRequestException("Title is required");
        }
        if (request.getPassingScore() != null
                && (request.getPassingScore() < 1 || request.getPassingScore() > 100)) {
            throw new InvalidRequestException("Passing score must be between 1 and 100");
        }
        log.debug("CreateQuizRequest validated for videoId={}", request.getVideoId());
    }
}