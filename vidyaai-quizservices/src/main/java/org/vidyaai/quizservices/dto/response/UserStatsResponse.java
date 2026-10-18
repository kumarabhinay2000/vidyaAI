package org.vidyaai.quizservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {

    private UUID userId;
    private Long totalAttempts;
    private Long completedAttempts;
    private Long passedAttempts;
    private Double passRate;
    private Double avgScore;
    private Integer totalPoints;
    private Integer rank;
}