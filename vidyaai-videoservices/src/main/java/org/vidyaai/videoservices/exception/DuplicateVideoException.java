package org.vidyaai.videoservices.exception;

import lombok.Getter;

@Getter
public class DuplicateVideoException extends VideoServiceException {

    private final String identifier;

    public DuplicateVideoException(String identifier) {
        super("Video already exists: " + identifier, "VID_011");
        this.identifier = identifier;
    }
}