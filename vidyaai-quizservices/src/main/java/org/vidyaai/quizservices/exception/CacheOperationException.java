package org.vidyaai.quizservices.exception;

public class CacheOperationException extends QuizServiceException {

    public CacheOperationException(String message) {
        super(message, "QUIZ_008");
    }

    public CacheOperationException(String message, Throwable cause) {
        super(message, "QUIZ_008", cause);
    }
}