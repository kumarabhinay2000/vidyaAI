package org.vidyaai.quizservices.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.quizservices.constant.CacheKeys;
import org.vidyaai.quizservices.constant.RedisTTL;
import org.vidyaai.quizservices.dto.response.LeaderboardEntryResponse;
import org.vidyaai.quizservices.dto.response.UserStatsResponse;
import org.vidyaai.quizservices.entity.Leaderboard;
import org.vidyaai.quizservices.entity.QuizAttempt;
import org.vidyaai.quizservices.exception.QuizServiceException;
import org.vidyaai.quizservices.mapper.LeaderboardMapper;
import org.vidyaai.quizservices.repository.LeaderboardRepository;
import org.vidyaai.quizservices.service.CacheService;
import org.vidyaai.quizservices.service.LeaderboardService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardServiceImpl implements LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final LeaderboardMapper leaderboardMapper;
    private final CacheService cacheService;

    @Override
    @Transactional
    public void recordAttempt(UUID userId, UUID quizId, QuizAttempt attempt) {
        try {
            Leaderboard entry = leaderboardRepository.findByUserId(userId)
                    .orElseGet(() -> Leaderboard.builder().userId(userId).build());

            entry.incrementQuizzesTaken();
            if (Boolean.TRUE.equals(attempt.getIsPassed())) {
                entry.incrementQuizzesPassed();
            }
            entry.addPoints(attempt.getScore() != null ? attempt.getScore() : 0);
            entry.recalculateAverage(attempt.getScore() != null ? attempt.getScore() : 0);

            leaderboardRepository.save(entry);
            cacheService.delete(CacheKeys.leaderboard());
            cacheService.delete(CacheKeys.userStats(userId.toString()));

            log.debug("Leaderboard updated for user={} (points={})",
                    userId, entry.getTotalPoints());
        } catch (Exception e) {
            log.error("Failed to record leaderboard for user {}: {}", userId, e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getTopPlayers(int limit) {
        try {
            String key = CacheKeys.leaderboard();
            @SuppressWarnings("unchecked")
            Optional<List<LeaderboardEntryResponse>> cached =
                    cacheService.get(key, (Class<List<LeaderboardEntryResponse>>) (Class<?>) List.class);
            if (cached.isPresent()) {
                return cached.get();
            }

            List<Leaderboard> top = leaderboardRepository.findTopPlayers(PageRequest.of(0, limit));
            List<LeaderboardEntryResponse> result = new ArrayList<>();
            int rank = 1;
            for (Leaderboard lb : top) {
                result.add(leaderboardMapper.toEntryResponse(lb, rank++));
            }

            cacheService.put(key, result, Duration.ofSeconds(RedisTTL.LEADERBOARD_SECONDS));
            return result;
        } catch (Exception e) {
            log.error("Failed to fetch leaderboard: {}", e.getMessage(), e);
            throw new QuizServiceException("Failed to fetch leaderboard", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatsResponse getUserStats(UUID userId) {
        try {
            String key = CacheKeys.userStats(userId.toString());
            Optional<UserStatsResponse> cached = cacheService.get(key, UserStatsResponse.class);
            if (cached.isPresent()) {
                return cached.get();
            }

            Leaderboard lb = leaderboardRepository.findByUserId(userId)
                    .orElse(Leaderboard.builder().userId(userId).build());

            UserStatsResponse response = leaderboardMapper.toUserStatsResponse(lb);
            cacheService.put(key, response, Duration.ofSeconds(RedisTTL.USER_STATS_SECONDS));
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch user stats for {}: {}", userId, e.getMessage(), e);
            throw new QuizServiceException("Failed to fetch user stats", e);
        }
    }
}