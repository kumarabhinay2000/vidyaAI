package org.vidyaai.videoservices.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class VideoNotFoundException extends VideoServiceException {

    private final UUID videoId;

    public VideoNotFoundException(UUID videoId) {
        super("Video not found with id: " + videoId, "VID_001");
        this.videoId = videoId;
    }

    public VideoNotFoundException(String message) {
        super(message, "VID_001");
        this.videoId = null;
    }
}