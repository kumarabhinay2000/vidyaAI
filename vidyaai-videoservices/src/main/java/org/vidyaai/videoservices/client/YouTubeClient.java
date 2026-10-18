package org.vidyaai.videoservices.client;

import org.vidyaai.videoservices.dto.response.YouTubeMetadataResponse;

public interface YouTubeClient {

    YouTubeMetadataResponse fetchMetadata(String youtubeUrl);

    String extractVideoId(String youtubeUrl);

    boolean isValidYouTubeUrl(String url);
}