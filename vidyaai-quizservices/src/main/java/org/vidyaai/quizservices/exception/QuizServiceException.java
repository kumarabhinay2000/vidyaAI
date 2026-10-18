package org.vidyaai.quizservices.exception;

import lombok.Getter;

@Getter
public class QuizServiceException extends RuntimeException {

    private final String errorCode;

    public QuizServiceException(String message) {
        super(message);
        this.errorCode = "QUIZ_999";
    }

    public QuizServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public QuizServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "QUIZ_999";
    }

    public QuizServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}