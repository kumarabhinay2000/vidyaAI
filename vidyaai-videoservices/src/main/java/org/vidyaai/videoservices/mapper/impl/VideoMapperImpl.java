package org.vidyaai.videoservices.mapper.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.videoservices.dto.response.VideoChapterResponse;
import org.vidyaai.videoservices.dto.response.VideoResponse;
import org.vidyaai.videoservices.dto.response.VideoSummaryResponse;
import org.vidyaai.videoservices.dto.response.VideoUploadResponse;
import org.vidyaai.videoservices.entity.Video;
import org.vidyaai.videoservices.entity.VideoChapter;
import org.vidyaai.videoservices.mapper.VideoMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class VideoMapperImpl implements VideoMapper {

    @Override
    public VideoResponse toResponse(Video entity, List<String> tags, List<VideoChapter> chapters) {
        try {
            return VideoResponse.builder()
                    .id(entity.getId())
                    .userId(entity.getUserId())
                    .title(entity.getTitle())
                    .description(entity.getDescription())
                    .sourceType(entity.getSourceType())
                    .sourceUrl(entity.getSourceUrl())
                    .youtubeVideoId(entity.getYoutubeVideoId())
                    .filePath(entity.getFilePath())
                    .fileSize(entity.getFileSize())
                    .contentType(entity.getContentType())
                    .durationSeconds(entity.getDurationSeconds())
                    .resolution(entity.getResolution())
                    .thumbnailUrl(entity.getThumbnailUrl())
                    .language(entity.getLanguage())
                    .status(entity.getStatus())
                    .processingProgress(entity.getProcessingProgress())
                    .viewCount(entity.getViewCount())
                    .tags(tags != null ? tags : Collections.emptyList())
                    .chapters(chapters != null
                            ? chapters.stream().map(this::toChapterResponse).toList()
                            : Collections.emptyList())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping Video to VideoResponse: {}", e.getMessage(), e);
            return VideoResponse.builder()
                    .id(entity.getId())
                    .userId(entity.getUserId())
                    .title(entity.getTitle())
                    .build();
        }
    }

    @Override
    public VideoSummaryResponse toSummaryResponse(Video entity) {
        return VideoSummaryResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .thumbnailUrl(entity.getThumbnailUrl())
                .sourceType(entity.getSourceType())
                .durationSeconds(entity.getDurationSeconds())
                .status(entity.getStatus())
                .processingProgress(entity.getProcessingProgress())
                .viewCount(entity.getViewCount())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public VideoUploadResponse toUploadResponse(Video entity) {
        return VideoUploadResponse.builder()
                .videoId(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .sourceType(entity.getSourceType())
                .status(entity.getStatus())
                .processingProgress(entity.getProcessingProgress())
                .message("Video uploaded and queued for processing")
                .uploadedAt(entity.getCreatedAt() != null
                        ? entity.getCreatedAt()
                        : LocalDateTime.now())
                .build();
    }

    @Override
    public VideoChapterResponse toChapterResponse(VideoChapter entity) {
        return VideoChapterResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .chapterOrder(entity.getChapterOrder())
                .build();
    }
}