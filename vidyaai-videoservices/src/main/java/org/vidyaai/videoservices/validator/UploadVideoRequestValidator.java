package org.vidyaai.videoservices.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.vidyaai.videoservices.dto.request.UploadVideoRequest;
import org.vidyaai.videoservices.exception.InvalidRequestException;
import org.vidyaai.videoservices.exception.UnsupportedFormatException;
import org.vidyaai.videoservices.exception.FileTooLargeException;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class UploadVideoRequestValidator {

    @Value("${video.upload.max-size-bytes:2147483648}")
    private long maxSizeBytes;

    @Value("${video.upload.allowed-mime-types:video/mp4,video/webm,video/x-matroska,video/quicktime,video/x-msvideo}")
    private String allowedMimeTypes;

    public void validate(UploadVideoRequest request, String filename, String contentType, long fileSize) {
        if (request == null) {
            throw new InvalidRequestException("Upload request cannot be null");
        }
        if (!StringUtils.hasText(request.getTitle())) {
            throw new InvalidRequestException("Title is required");
        }
        if (fileSize <= 0) {
            throw new InvalidRequestException("File is empty");
        }
        if (fileSize > maxSizeBytes) {
            throw new FileTooLargeException(maxSizeBytes, fileSize);
        }

        List<String> allowed = Arrays.asList(allowedMimeTypes.split(","));
        if (!StringUtils.hasText(contentType) || !allowed.contains(contentType.trim())) {
            throw new UnsupportedFormatException(contentType);
        }

        log.debug("UploadVideoRequest validated for file={} contentType={} size={}",
                filename, contentType, fileSize);
    }
}