package org.vidyaai.quizservices.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class QuizExpiredException extends QuizServiceException {

    private final UUID quizId;

    public QuizExpiredException(UUID quizId) {
        super("Quiz has expired: " + quizId, "QUIZ_006");
        this.quizId = quizId;
    }
}