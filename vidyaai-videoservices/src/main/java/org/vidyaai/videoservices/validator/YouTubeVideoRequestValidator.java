package org.vidyaai.videoservices.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.vidyaai.videoservices.dto.request.YouTubeVideoRequest;
import org.vidyaai.videoservices.exception.InvalidRequestException;

import java.util.regex.Pattern;

@Component
@Slf4j
public class YouTubeVideoRequestValidator {

    private static final Pattern YT_PATTERN = Pattern.compile(
            "^(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/|" +
                    "youtube\\.com/embed/|youtube\\.com/shorts/)[\\w-]{11}.*$"
    );

    public void validate(YouTubeVideoRequest request) {
        if (request == null) {
            throw new InvalidRequestException("YouTube request cannot be null");
        }
        if (!StringUtils.hasText(request.getYoutubeUrl())) {
            throw new InvalidRequestException("YouTube URL is required");
        }
        if (!YT_PATTERN.matcher(request.getYoutubeUrl().trim()).matches()) {
            throw new InvalidRequestException("Invalid YouTube URL format");
        }
        log.debug("YouTubeVideoRequest validated for URL={}", request.getYoutubeUrl());
    }
}