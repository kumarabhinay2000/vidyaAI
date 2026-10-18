package org.vidyaai.quizservices.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.quizservices.dto.response.ApiResponse;
import org.vidyaai.quizservices.dto.response.LeaderboardEntryResponse;
import org.vidyaai.quizservices.dto.response.UserStatsResponse;
import org.vidyaai.quizservices.security.SecurityUtils;
import org.vidyaai.quizservices.service.LeaderboardService;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes/leaderboard")
@RequiredArgsConstructor
@Slf4j
public class LeaderboardController {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 100;

    private final LeaderboardService leaderboardService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<LeaderboardEntryResponse>>> getTopPlayers(
            @RequestParam(defaultValue = "10") int limit) {
        int bounded = Math.min(Math.max(limit, 1), MAX_LIMIT);
        log.info("GET /api/quizzes/leaderboard?limit={}", bounded);
        List<LeaderboardEntryResponse> response = leaderboardService.getTopPlayers(bounded);
        return ResponseEntity.ok(ApiResponse.success(response, "Leaderboard fetched"));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getMyStats() {
        var userId = securityUtils.getCurrentUserId();
        log.info("GET /api/quizzes/leaderboard/me for userId={}", userId);
        UserStatsResponse response = leaderboardService.getUserStats(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "User stats fetched"));
    }
}