package org.vidyaai.quizservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnalyticsResponse {

    private UUID quizId;
    private Integer totalAttempts;
    private Double avgScore;
    private Integer highestScore;
    private Integer lowestScore;
    private Double passRate;
    private Integer avgTimeTaken;
    private Map<UUID, Double> difficultQuestions;
    private Map<UUID, Double> easyQuestions;
    private LocalDateTime updatedAt;
}