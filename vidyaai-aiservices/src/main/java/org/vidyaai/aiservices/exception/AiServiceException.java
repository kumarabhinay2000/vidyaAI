package org.vidyaai.aiservices.exception;

import lombok.Getter;

@Getter
public class AiServiceException extends RuntimeException {

    private final String errorCode;

    public AiServiceException(String message) {
        super(message);
        this.errorCode = "AI_999";
    }

    public AiServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AiServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "AI_999";
    }

    public AiServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}