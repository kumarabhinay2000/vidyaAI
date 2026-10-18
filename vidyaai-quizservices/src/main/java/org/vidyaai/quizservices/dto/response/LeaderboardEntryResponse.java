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
public class LeaderboardEntryResponse {

    private Integer rank;
    private UUID userId;
    private Integer totalPoints;
    private Integer quizzesTaken;
    private Integer quizzesPassed;
    private Double avgScore;
}