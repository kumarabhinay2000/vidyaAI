package org.vidyaai.aiservices.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.dto.request.TranscriptRequest;
import org.vidyaai.aiservices.exception.InvalidRequestException;

@Component
@Slf4j
public class TranscriptRequestValidator {

    public void validate(TranscriptRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Transcript request cannot be null");
        }
        if (request.getVideoId() == null) {
            throw new InvalidRequestException("Video ID is required");
        }
        log.debug("TranscriptRequest validated for video: {}", request.getVideoId());
    }
}