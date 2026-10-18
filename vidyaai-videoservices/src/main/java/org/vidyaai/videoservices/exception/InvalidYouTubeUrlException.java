package org.vidyaai.videoservices.exception;

import lombok.Getter;

@Getter
public class InvalidYouTubeUrlException extends VideoServiceException {

    private final String url;

    public InvalidYouTubeUrlException(String url) {
        super("Invalid YouTube URL: " + url, "VID_007");
        this.url = url;
    }
}