package org.vidyaai.videoservices.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidRequestException extends VideoServiceException {

    private final Map<String, String> errors;

    public InvalidRequestException(String message) {
        super(message, "VID_002");
        this.errors = null;
    }

    public InvalidRequestException(String message, Map<String, String> errors) {
        super(message, "VID_002");
        this.errors = errors;
    }
}