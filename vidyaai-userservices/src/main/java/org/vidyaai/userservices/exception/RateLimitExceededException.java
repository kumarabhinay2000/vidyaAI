package org.vidyaai.userservices.exception;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {

    private final String errorCode;

    public RateLimitExceededException(String message) {
        super(message);
        this.errorCode = "RATE_001";
    }

    public RateLimitExceededException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}