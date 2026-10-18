package org.vidyaai.videoservices.exception;

public class CacheOperationException extends VideoServiceException {

    public CacheOperationException(String message) {
        super(message, "VID_010");
    }

    public CacheOperationException(String message, Throwable cause) {
        super(message, "VID_010", cause);
    }
}