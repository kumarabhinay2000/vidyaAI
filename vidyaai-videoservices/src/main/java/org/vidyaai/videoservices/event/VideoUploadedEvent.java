package org.vidyaai.videoservices.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadedEvent {

    private UUID eventId;
    private UUID videoId;
    private UUID userId;
    private String sourceType;
    private String sourceUrl;
    private String filePath;
    private String title;
    private Integer durationSeconds;
    private LocalDateTime uploadedAt;
}