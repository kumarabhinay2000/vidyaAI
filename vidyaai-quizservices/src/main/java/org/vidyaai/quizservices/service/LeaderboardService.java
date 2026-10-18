package org.vidyaai.quizservices.service;

import org.vidyaai.quizservices.dto.response.LeaderboardEntryResponse;
import org.vidyaai.quizservices.dto.response.UserStatsResponse;
import org.vidyaai.quizservices.entity.QuizAttempt;

import java.util.List;
import java.util.UUID;

public interface LeaderboardService {

    void recordAttempt(UUID userId, UUID quizId, QuizAttempt attempt);

    List<LeaderboardEntryResponse> getTopPlayers(int limit);

    UserStatsResponse getUserStats(UUID userId);
}