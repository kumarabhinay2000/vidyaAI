package org.vidyaai.videoservices.exception;

public class UploadFailedException extends VideoServiceException {

    public UploadFailedException(String message) {
        super(message, "VID_008");
    }

    public UploadFailedException(String message, Throwable cause) {
        super(message, "VID_008", cause);
    }
}