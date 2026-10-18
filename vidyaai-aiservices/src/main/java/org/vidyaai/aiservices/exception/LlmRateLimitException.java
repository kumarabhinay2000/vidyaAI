package org.vidyaai.aiservices.exception;

import lombok.Getter;

@Getter
public class LlmRateLimitException extends AiServiceException {

    private final Long retryAfterSeconds;

    public LlmRateLimitException(String message, Long retryAfterSeconds) {
        super(message, "AI_004");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public LlmRateLimitException(String message, Long retryAfterSeconds, Throwable cause) {
        super(message, "AI_004", cause);
        this.retryAfterSeconds = retryAfterSeconds;
    }
}