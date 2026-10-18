package org.vidyaai.videoservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.videoservices.enums.VideoStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoStatusResponse {

    private UUID videoId;
    private VideoStatus status;
    private Integer progress;
    private String currentStep;
    private String message;
    private LocalDateTime updatedAt;
}