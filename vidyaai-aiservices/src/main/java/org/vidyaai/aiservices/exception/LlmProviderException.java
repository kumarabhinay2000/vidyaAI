package org.vidyaai.aiservices.exception;

import lombok.Getter;

@Getter
public class LlmProviderException extends AiServiceException {

    private final String provider;

    public LlmProviderException(String provider, String message) {
        super(message, "AI_002");
        this.provider = provider;
    }

    public LlmProviderException(String provider, String message, Throwable cause) {
        super(message, "AI_002", cause);
        this.provider = provider;
    }
}