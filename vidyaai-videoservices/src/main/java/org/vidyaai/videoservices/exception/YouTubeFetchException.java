package org.vidyaai.videoservices.exception;

public class YouTubeFetchException extends VideoServiceException {

    public YouTubeFetchException(String message) {
        super(message, "VID_006");
    }

    public YouTubeFetchException(String message, Throwable cause) {
        super(message, "VID_006", cause);
    }
}