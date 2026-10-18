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
public class AiProcessingFailedEvent {

    private UUID eventId;
    private UUID videoId;
    private UUID userId;
    private String errorCode;
    private String errorMessage;
    private String step;
    private Integer retryCount;
    private LocalDateTime failedAt;
}