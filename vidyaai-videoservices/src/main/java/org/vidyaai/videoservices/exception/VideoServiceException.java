package org.vidyaai.videoservices.exception;

import lombok.Getter;

@Getter
public class VideoServiceException extends RuntimeException {

    private final String errorCode;

    public VideoServiceException(String message) {
        super(message);
        this.errorCode = "VID_999";
    }

    public VideoServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public VideoServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "VID_999";
    }

    public VideoServiceException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}