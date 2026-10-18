package org.vidyaai.videoservices.exception;

public class StorageException extends VideoServiceException {

    public StorageException(String message) {
        super(message, "VID_005");
    }

    public StorageException(String message, Throwable cause) {
        super(message, "VID_005", cause);
    }
}