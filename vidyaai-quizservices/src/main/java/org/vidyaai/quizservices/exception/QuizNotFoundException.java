package org.vidyaai.quizservices.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class QuizNotFoundException extends QuizServiceException {

    private final UUID quizId;

    public QuizNotFoundException(UUID quizId) {
        super("Quiz not found with id: " + quizId, "QUIZ_001");
        this.quizId = quizId;
    }

    public QuizNotFoundException(String message) {
        super(message, "QUIZ_001");
        this.quizId = null;
    }
}