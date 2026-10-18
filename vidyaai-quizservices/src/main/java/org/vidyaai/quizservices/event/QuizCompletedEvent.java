package org.vidyaai.quizservices.event;

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
public class QuizCompletedEvent {

    private UUID eventId;
    private UUID attemptId;
    private UUID quizId;
    private UUID userId;
    private UUID videoId;
    private Integer finalScore;
    private Double percentage;
    private Boolean passed;
    private Integer timeTakenSeconds;
    private LocalDateTime completedAt;
}
