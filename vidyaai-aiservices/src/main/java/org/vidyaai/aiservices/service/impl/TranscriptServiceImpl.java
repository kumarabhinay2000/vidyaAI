package org.vidyaai.aiservices.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.aiservices.client.TranscriptClient;
import org.vidyaai.aiservices.constant.CacheKeys;
import org.vidyaai.aiservices.constant.RedisTTL;
import org.vidyaai.aiservices.dto.request.TranscriptRequest;
import org.vidyaai.aiservices.dto.response.TranscriptResponse;
import org.vidyaai.aiservices.exception.ResourceNotFoundException;
import org.vidyaai.aiservices.exception.TranscriptExtractionException;
import org.vidyaai.aiservices.service.CacheService;
import org.vidyaai.aiservices.service.TranscriptService;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranscriptServiceImpl implements TranscriptService {

    private final TranscriptClient transcriptClient;
    private final CacheService cacheService;

    @Override
    @Transactional(readOnly = true)
    public TranscriptResponse getTranscript(UUID videoId) {
        try {
            String cacheKey = CacheKeys.transcript(videoId.toString());
            Optional<TranscriptResponse> cached = cacheService.get(cacheKey, TranscriptResponse.class);
            if (cached.isPresent()) {
                TranscriptResponse response = cached.get();
                response.setCached(true);
                log.debug("Transcript cache HIT for videoId={}", videoId);
                return response;
            }
            throw new ResourceNotFoundException("Transcript", "videoId", videoId);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting transcript for videoId {}: {}", videoId, e.getMessage(), e);
            throw new TranscriptExtractionException("Failed to get transcript", e);
        }
    }

    @Override
    @Transactional
    public TranscriptResponse extractTranscript(TranscriptRequest request) {
        log.info("Extracting transcript for videoId={}, sourceUrl={}",
                request.getVideoId(), request.getSourceUrl());
        try {
            String cacheKey = CacheKeys.transcript(request.getVideoId().toString());

            if (!Boolean.TRUE.equals(request.getForceRegenerate())) {
                Optional<TranscriptResponse> cached = cacheService.get(cacheKey, TranscriptResponse.class);
                if (cached.isPresent()) {
                    TranscriptResponse response = cached.get();
                    response.setCached(true);
                    log.info("Returning cached transcript for videoId={}", request.getVideoId());
                    return response;
                }
            }

            TranscriptResponse response;
            if (request.getSourceUrl() != null
                    && transcriptClient.isYouTubeUrl(request.getSourceUrl())) {
                response = transcriptClient.extractFromYouTube(request.getSourceUrl(), request.getVideoId());
            } else if (request.getSourceUrl() != null) {
                response = transcriptClient.extractFromFile(request.getSourceUrl(), request.getVideoId());
            } else {
                throw new TranscriptExtractionException("Source URL/path is required");
            }

            cacheService.put(cacheKey, response, Duration.ofSeconds(RedisTTL.TRANSCRIPT_SECONDS));
            log.info("Transcript extracted and cached for videoId={}", request.getVideoId());
            return response;
        } catch (TranscriptExtractionException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error extracting transcript for videoId {}: {}",
                    request.getVideoId(), e.getMessage(), e);
            throw new TranscriptExtractionException("Failed to extract transcript", e);
        }
    }

    @Override
    @Transactional
    public void deleteTranscript(UUID videoId) {
        try {
            String cacheKey = CacheKeys.transcript(videoId.toString());
            cacheService.delete(cacheKey);
            log.info("Transcript deleted for videoId={}", videoId);
        } catch (Exception e) {
            log.error("Error deleting transcript for videoId {}: {}", videoId, e.getMessage(), e);
            throw new TranscriptExtractionException("Failed to delete transcript", e);
        }
    }

    @Override
    public boolean hasTranscript(UUID videoId) {
        try {
            String cacheKey = CacheKeys.transcript(videoId.toString());
            return cacheService.exists(cacheKey);
        } catch (Exception e) {
            log.warn("Error checking transcript existence for videoId {}: {}", videoId, e.getMessage());
            return false;
        }
    }
}