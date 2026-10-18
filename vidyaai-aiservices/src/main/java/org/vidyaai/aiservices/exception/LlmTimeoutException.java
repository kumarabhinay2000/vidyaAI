package org.vidyaai.aiservices.exception;

import lombok.Getter;

@Getter
public class LlmTimeoutException extends AiServiceException {

    private final Long timeoutMs;

    public LlmTimeoutException(String message, Long timeoutMs) {
        super(message, "AI_003");
        this.timeoutMs = timeoutMs;
    }

    public LlmTimeoutException(String message, Long timeoutMs, Throwable cause) {
        super(message, "AI_003", cause);
        this.timeoutMs = timeoutMs;
    }
}