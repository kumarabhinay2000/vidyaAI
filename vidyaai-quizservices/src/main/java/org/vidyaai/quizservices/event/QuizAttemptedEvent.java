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
public class QuizAttemptedEvent {

    private UUID eventId;
    private UUID attemptId;
    private UUID quizId;
    private UUID userId;
    private UUID videoId;
    private Integer score;
    private Integer totalMarks;
    private Double percentage;
    private Boolean isPassed;
    private LocalDateTime attemptedAt;
}