package org.vidyaai.aiservices.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidRequestException extends AiServiceException {

    private final Map<String, String> errors;

    public InvalidRequestException(String message) {
        super(message, "AI_007");
        this.errors = null;
    }

    public InvalidRequestException(String message, Map<String, String> errors) {
        super(message, "AI_007");
        this.errors = errors;
    }
}