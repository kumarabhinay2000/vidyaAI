package org.vidyaai.videoservices.exception;

import lombok.Getter;

@Getter
public class UnsupportedFormatException extends VideoServiceException {

    private final String contentType;

    public UnsupportedFormatException(String contentType) {
        super("Unsupported video format: " + contentType, "VID_004");
        this.contentType = contentType;
    }
}