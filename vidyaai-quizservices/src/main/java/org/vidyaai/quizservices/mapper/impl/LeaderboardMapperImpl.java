package org.vidyaai.quizservices.mapper.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.quizservices.dto.response.LeaderboardEntryResponse;
import org.vidyaai.quizservices.dto.response.UserStatsResponse;
import org.vidyaai.quizservices.entity.Leaderboard;
import org.vidyaai.quizservices.mapper.LeaderboardMapper;

@Component
@Slf4j
public class LeaderboardMapperImpl implements LeaderboardMapper {

    @Override
    public LeaderboardEntryResponse toEntryResponse(Leaderboard entity, int rank) {
        return LeaderboardEntryResponse.builder()
                .rank(rank)
                .userId(entity.getUserId())
                .totalPoints(entity.getTotalPoints())
                .quizzesTaken(entity.getQuizzesTaken())
                .quizzesPassed(entity.getQuizzesPassed())
                .avgScore(entity.getAvgScore())
                .build();
    }

    @Override
    public UserStatsResponse toUserStatsResponse(Leaderboard entity) {
        double passRate = 0.0;
        if (entity.getQuizzesTaken() != null && entity.getQuizzesTaken() > 0) {
            passRate = (entity.getQuizzesPassed() * 100.0) / entity.getQuizzesTaken();
        }

        return UserStatsResponse.builder()
                .userId(entity.getUserId())
                .totalAttempts(entity.getQuizzesTaken() != null
                        ? entity.getQuizzesTaken().longValue() : 0L)
                .completedAttempts(entity.getQuizzesTaken() != null
                        ? entity.getQuizzesTaken().longValue() : 0L)
                .passedAttempts(entity.getQuizzesPassed() != null
                        ? entity.getQuizzesPassed().longValue() : 0L)
                .passRate(passRate)
                .avgScore(entity.getAvgScore())
                .totalPoints(entity.getTotalPoints())
                .rank(entity.getRank())
                .build();
    }
}