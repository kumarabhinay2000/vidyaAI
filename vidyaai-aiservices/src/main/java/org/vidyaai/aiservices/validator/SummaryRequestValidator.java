package org.vidyaai.aiservices.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.dto.request.SummaryRequest;
import org.vidyaai.aiservices.exception.InvalidRequestException;

@Component
@Slf4j
public class SummaryRequestValidator {

    public void validate(SummaryRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Summary request cannot be null");
        }
        if (request.getVideoId() == null) {
            throw new InvalidRequestException("Video ID is required");
        }
        if (request.getType() == null) {
            throw new InvalidRequestException("Summary type is required");
        }
        log.debug("SummaryRequest validated for video: {}", request.getVideoId());
    }
}