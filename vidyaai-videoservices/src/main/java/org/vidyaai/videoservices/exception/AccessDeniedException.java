package org.vidyaai.videoservices.exception;

public class AccessDeniedException extends VideoServiceException {

    public AccessDeniedException(String message) {
        super(message, "VID_009");
    }

    public AccessDeniedException() {
        super("Access denied to this video resource", "VID_009");
    }
}