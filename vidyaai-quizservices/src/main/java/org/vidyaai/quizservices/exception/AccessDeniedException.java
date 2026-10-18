package org.vidyaai.quizservices.exception;

public class AccessDeniedException extends QuizServiceException {

    public AccessDeniedException(String message) {
        super(message, "QUIZ_007");
    }

    public AccessDeniedException() {
        super("Access denied to this resource", "QUIZ_007");
    }
}