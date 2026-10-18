package org.vidyaai.aiservices.exception;

public class AuthenticationException extends AiServiceException {

    public AuthenticationException(String message) {
        super(message, "AUTH_001");
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, "AUTH_001", cause);
    }
}
