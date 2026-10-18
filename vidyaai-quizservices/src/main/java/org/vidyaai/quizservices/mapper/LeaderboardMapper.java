package org.vidyaai.quizservices.mapper;

import org.vidyaai.quizservices.dto.response.LeaderboardEntryResponse;
import org.vidyaai.quizservices.dto.response.UserStatsResponse;
import org.vidyaai.quizservices.entity.Leaderboard;

public interface LeaderboardMapper {

    LeaderboardEntryResponse toEntryResponse(Leaderboard entity, int rank);

    UserStatsResponse toUserStatsResponse(Leaderboard entity);
}