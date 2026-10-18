package org.vidyaai.quizservices.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AttemptNotFoundException extends QuizServiceException {

    private final UUID attemptId;

    public AttemptNotFoundException(UUID attemptId) {
        super("Quiz attempt not found with id: " + attemptId, "QUIZ_002");
        this.attemptId = attemptId;
    }
}