package org.vidyaai.videoservices.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.vidyaai.videoservices.dto.request.UpdateVideoRequest;
import org.vidyaai.videoservices.exception.InvalidRequestException;

@Component
@Slf4j
public class UpdateVideoRequestValidator {

    public void validate(UpdateVideoRequest request) {
        if (request == null) {
            throw new InvalidRequestException("Update request cannot be null");
        }
        if (!StringUtils.hasText(request.getTitle())
                && !StringUtils.hasText(request.getDescription())
                && !StringUtils.hasText(request.getLanguage())) {
            throw new InvalidRequestException("At least one field must be provided for update");
        }
        if (StringUtils.hasText(request.getTitle())
                && request.getTitle().length() > 500) {
            throw new InvalidRequestException("Title must not exceed 500 characters");
        }
        log.debug("UpdateVideoRequest validated");
    }
}