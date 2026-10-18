package org.vidyaai.aiservices.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.client.TranscriptClient;
import org.vidyaai.aiservices.dto.response.TranscriptResponse;
import org.vidyaai.aiservices.exception.TranscriptExtractionException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class TranscriptClientImpl implements TranscriptClient {

    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(
            "^(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[\\w-]{11}.*$"
    );

    @Override
    public TranscriptResponse extractFromYouTube(String youtubeUrl, UUID videoId) {
        try {
            log.info("Extracting transcript from YouTube: {} for videoId={}", youtubeUrl, videoId);
            if (!isYouTubeUrl(youtubeUrl)) {
                throw new TranscriptExtractionException("Invalid YouTube URL: " + youtubeUrl);
            }

            // NOTE: Real integration would use YouTube Data API / third-party transcript API.
            // This method is a placeholder that will be replaced when the transcript provider
            // is wired in. Returning a structured response with empty transcript so that
            // downstream services can handle gracefully.
            String transcript = "";

            return TranscriptResponse.builder()
                    .videoId(videoId)
                    .transcript(transcript)
                    .language("en")
                    .wordCount(0)
                    .characterCount(0)
                    .cached(false)
                    .segments(Collections.emptyList())
                    .createdAt(LocalDateTime.now())
                    .build();
        } catch (TranscriptExtractionException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to extract YouTube transcript for {}: {}", youtubeUrl, e.getMessage(), e);
            throw new TranscriptExtractionException("YouTube transcript extraction failed", e);
        }
    }

    @Override
    public TranscriptResponse extractFromFile(String filePath, UUID videoId) {
        try {
            log.info("Extracting transcript from file: {} for videoId={}", filePath, videoId);
            if (filePath == null || filePath.isBlank()) {
                throw new TranscriptExtractionException("File path cannot be blank");
            }

            // NOTE: Real integration would use AssemblyAI / Whisper / Tika-based text extraction.
            String transcript = "";

            return TranscriptResponse.builder()
                    .videoId(videoId)
                    .transcript(transcript)
                    .language("en")
                    .wordCount(0)
                    .characterCount(0)
                    .cached(false)
                    .segments(Collections.emptyList())
                    .createdAt(LocalDateTime.now())
                    .build();
        } catch (TranscriptExtractionException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to extract transcript from file {}: {}", filePath, e.getMessage(), e);
            throw new TranscriptExtractionException("File transcript extraction failed", e);
        }
    }

    @Override
    public boolean isYouTubeUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        return YOUTUBE_PATTERN.matcher(url.trim()).matches();
    }
}