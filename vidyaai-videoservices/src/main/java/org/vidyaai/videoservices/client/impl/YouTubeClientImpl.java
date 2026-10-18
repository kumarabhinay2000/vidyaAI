package org.vidyaai.videoservices.client.impl;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.vidyaai.videoservices.client.YouTubeClient;
import org.vidyaai.videoservices.dto.response.YouTubeMetadataResponse;
import org.vidyaai.videoservices.exception.InvalidYouTubeUrlException;
import org.vidyaai.videoservices.exception.YouTubeFetchException;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class YouTubeClientImpl implements YouTubeClient {

    private static final Pattern[] URL_PATTERNS = new Pattern[]{
            Pattern.compile("youtube\\.com/watch\\?v=([\\w-]{11})"),
            Pattern.compile("youtu\\.be/([\\w-]{11})"),
            Pattern.compile("youtube\\.com/embed/([\\w-]{11})"),
            Pattern.compile("youtube\\.com/shorts/([\\w-]{11})")
    };

    @Value("${youtube.api.key:}")
    private String apiKey;

    @Override
    public boolean isValidYouTubeUrl(String url) {
        return extractVideoId(url) != null;
    }

    @Override
    public String extractVideoId(String youtubeUrl) {
        if (!StringUtils.hasText(youtubeUrl)) {
            return null;
        }
        for (Pattern pattern : URL_PATTERNS) {
            Matcher matcher = pattern.matcher(youtubeUrl);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    @Override
    @CircuitBreaker(name = "youtubeApi", fallbackMethod = "fetchMetadataFallback")
    @Retry(name = "youtubeApiRetry")
    public YouTubeMetadataResponse fetchMetadata(String youtubeUrl) {
        String videoId = extractVideoId(youtubeUrl);
        if (videoId == null) {
            throw new InvalidYouTubeUrlException(youtubeUrl);
        }
        if (!StringUtils.hasText(apiKey)) {
            log.warn("YouTube API key not configured; returning minimal metadata for {}", videoId);
            return YouTubeMetadataResponse.builder()
                    .videoId(videoId)
                    .title("YouTube video " + videoId)
                    .thumbnailUrl("https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg")
                    .build();
        }
        try {
            YouTube youtube = new YouTube.Builder(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    request -> {
                    }).setApplicationName("vidyaai-video-service").build();

            YouTube.Videos.List request = youtube.videos()
                    .list(Collections.singletonList("snippet,contentDetails,statistics"))
                    .setId(Collections.singletonList(videoId))
                    .setKey(apiKey);

            VideoListResponse response = request.execute();
            List<Video> items = response.getItems();
            if (items == null || items.isEmpty()) {
                throw new YouTubeFetchException("YouTube video not found: " + videoId);
            }
            Video video = items.get(0);
            return YouTubeMetadataResponse.builder()
                    .videoId(videoId)
                    .title(video.getSnippet().getTitle())
                    .description(video.getSnippet().getDescription())
                    .channelTitle(video.getSnippet().getChannelTitle())
                    .thumbnailUrl(video.getSnippet().getThumbnails().getHigh().getUrl())
                    .durationSeconds(parseIsoDuration(video.getContentDetails().getDuration()))
                    .viewCount(video.getStatistics().getViewCount() != null
                            ? video.getStatistics().getViewCount().longValue() : 0L)
                    .publishedAt(video.getSnippet().getPublishedAt().toString())
                    .language(video.getSnippet().getDefaultAudioLanguage())
                    .build();
        } catch (YouTubeFetchException e) {
            throw e;
        } catch (Exception e) {
            log.error("YouTube fetch failed for {}: {}", youtubeUrl, e.getMessage(), e);
            throw new YouTubeFetchException("YouTube metadata fetch failed", e);
        }
    }

    private Integer parseIsoDuration(String iso) {
        if (iso == null) {
            return null;
        }
        try {
            java.time.Duration d = java.time.Duration.parse(iso);
            return (int) d.getSeconds();
        } catch (Exception e) {
            return null;
        }
    }

    public YouTubeMetadataResponse fetchMetadataFallback(String youtubeUrl, Throwable t) {
        log.error("Fallback: YouTube metadata fetch failed for {} - {}",
                youtubeUrl, t.getMessage());
        throw new YouTubeFetchException("YouTube API unavailable", t);
    }
}