package org.vidyaai.videoservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.videoservices.enums.SourceType;
import org.vidyaai.videoservices.enums.VideoStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoSummaryResponse {

    private UUID id;
    private UUID userId;
    private String title;
    private String thumbnailUrl;
    private SourceType sourceType;
    private Integer durationSeconds;
    private VideoStatus status;
    private Integer processingProgress;
    private Integer viewCount;
    private LocalDateTime createdAt;
}