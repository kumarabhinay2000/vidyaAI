package org.vidyaai.aiservices.exception;

public class CacheOperationException extends AiServiceException {

    public CacheOperationException(String message) {
        super(message, "AI_008");
    }

    public CacheOperationException(String message, Throwable cause) {
        super(message, "AI_008", cause);
    }
}