package org.vidyaai.userservices.exception;

import lombok.Getter;
import java.util.Map;

@Getter
public class InvalidRequestException extends RuntimeException {

    private final Map<String, String> errors;
    private final String errorCode;

    public InvalidRequestException(String message) {
        super(message);
        this.errors = null;
        this.errorCode = "INVALID_REQUEST";
    }

    public InvalidRequestException(String message, String errorCode) {
        super(message);
        this.errors = null;
        this.errorCode = errorCode;
    }

    public InvalidRequestException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
        this.errorCode = "VALIDATION_ERROR";
    }

    public InvalidRequestException(String message, Map<String, String> errors, String errorCode) {
        super(message);
        this.errors = errors;
        this.errorCode = errorCode;
    }

    public Map<String, String> getError() {
        return errors;
    }
}