package org.vidyaai.quizservices.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiProcessingCompletedEvent {

    private UUID eventId;
    private UUID videoId;
    private UUID userId;
    private List<String> contentTypes;
    private UUID summaryId;
    private UUID notesId;
    private UUID quizId;
    private Long processingTimeMs;
    private Boolean cacheHit;
    private LocalDateTime completedAt;
}