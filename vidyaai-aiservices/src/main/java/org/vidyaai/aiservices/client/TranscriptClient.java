package org.vidyaai.aiservices.client;

import org.vidyaai.aiservices.dto.response.TranscriptResponse;

import java.util.UUID;

public interface TranscriptClient {

    TranscriptResponse extractFromYouTube(String youtubeUrl, UUID videoId);

    TranscriptResponse extractFromFile(String filePath, UUID videoId);

    boolean isYouTubeUrl(String url);
}