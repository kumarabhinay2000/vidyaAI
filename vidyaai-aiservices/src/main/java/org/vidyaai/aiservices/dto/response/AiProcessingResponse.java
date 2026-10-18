package org.vidyaai.aiservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.aiservices.enums.ProcessingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiProcessingResponse {

    private UUID requestId;
    private UUID videoId;
    private ProcessingStatus status;
    private Integer progress;
    private String message;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}