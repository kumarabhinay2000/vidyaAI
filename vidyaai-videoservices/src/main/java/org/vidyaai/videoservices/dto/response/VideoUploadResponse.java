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
public class VideoUploadResponse {

    private UUID videoId;
    private UUID userId;
    private String title;
    private SourceType sourceType;
    private VideoStatus status;
    private Integer processingProgress;
    private String message;
    private LocalDateTime uploadedAt;
}