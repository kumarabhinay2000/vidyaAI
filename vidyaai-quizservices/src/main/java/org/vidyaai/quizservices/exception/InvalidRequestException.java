package org.vidyaai.quizservices.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidRequestException extends QuizServiceException {

    private final Map<String, String> errors;

    public InvalidRequestException(String message) {
        super(message, "QUIZ_003");
        this.errors = null;
    }

    public InvalidRequestException(String message, Map<String, String> errors) {
        super(message, "QUIZ_003");
        this.errors = errors;
    }
}