package org.vidyaai.aiservices.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.dto.request.ChatbotRequest;
import org.vidyaai.aiservices.exception.InvalidRequestException;

@Component
@Slf4j
public class ChatbotRequestValidator {

    private static final int MAX_QUESTION_LENGTH = 2000;

    public void validate(ChatbotRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Chatbot request cannot be null");
        }
        if (request.getVideoId() == null) {
            throw new InvalidRequestException("Video ID is required");
        }
        if (request.getQuestion() == null || request.getQuestion().isBlank()) {
            throw new InvalidRequestException("Question cannot be empty");
        }
        if (request.getQuestion().length() > MAX_QUESTION_LENGTH) {
            throw new InvalidRequestException(
                    "Question must not exceed " + MAX_QUESTION_LENGTH + " characters");
        }
        log.debug("ChatbotRequest validated for video: {}", request.getVideoId());
    }
}