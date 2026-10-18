package org.vidyaai.aiservices.service;

import org.vidyaai.aiservices.dto.request.TranscriptRequest;
import org.vidyaai.aiservices.dto.response.TranscriptResponse;

import java.util.UUID;

public interface TranscriptService {

    TranscriptResponse getTranscript(UUID videoId);

    TranscriptResponse extractTranscript(TranscriptRequest request);

    void deleteTranscript(UUID videoId);

    boolean hasTranscript(UUID videoId);
}