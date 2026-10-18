package org.vidyaai.quizservices.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MaxAttemptsReachedException extends QuizServiceException {

    private final UUID quizId;
    private final UUID userId;
    private final long attemptsMade;

    public MaxAttemptsReachedException(UUID quizId, UUID userId, long attemptsMade) {
        super("Maximum attempts reached for quiz " + quizId + " by user " + userId
                + " (attempts: " + attemptsMade + ")", "QUIZ_005");
        this.quizId = quizId;
        this.userId = userId;
        this.attemptsMade = attemptsMade;
    }
}