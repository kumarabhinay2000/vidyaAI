package org.vidyaai.quizservices.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AttemptCooldownException extends QuizServiceException {

    private final UUID quizId;
    private final UUID userId;
    private final long cooldownMinutes;

    public AttemptCooldownException(UUID quizId, UUID userId, long cooldownMinutes) {
        super("Attempt cooldown active for quiz " + quizId + " by user " + userId
                + " (" + cooldownMinutes + " minutes remaining)", "QUIZ_004");
        this.quizId = quizId;
        this.userId = userId;
        this.cooldownMinutes = cooldownMinutes;
    }
}