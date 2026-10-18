package org.vidyaai.quizservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.quizservices.enums.AttemptStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptResponse {

    private UUID id;
    private UUID quizId;
    private UUID userId;
    private Integer score;
    private Integer totalMarks;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer unanswered;
    private Double percentage;
    private Integer timeTakenSeconds;
    private AttemptStatus status;
    private Boolean isPassed;
    private Map<UUID, String> answers;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
}