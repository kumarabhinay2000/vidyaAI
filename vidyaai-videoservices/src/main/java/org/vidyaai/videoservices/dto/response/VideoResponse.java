package org.vidyaai.videoservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.videoservices.enums.SourceType;
import org.vidyaai.videoservices.enums.VideoStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponse {

    private UUID id;
    private UUID userId;
    private String title;
    private String description;
    private SourceType sourceType;
    private String sourceUrl;
    private String youtubeVideoId;
    private String filePath;
    private Long fileSize;
    private String contentType;
    private Integer durationSeconds;
    private String resolution;
    private String thumbnailUrl;
    private String language;
    private VideoStatus status;
    private Integer processingProgress;
    private Integer viewCount;
    private List<String> tags;
    private List<VideoChapterResponse> chapters;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}