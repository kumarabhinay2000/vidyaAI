package org.vidyaai.quizservices.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.quizservices.dto.response.ApiResponse;
import org.vidyaai.quizservices.dto.response.QuizAnalyticsResponse;
import org.vidyaai.quizservices.service.AnalyticsService;

import java.util.UUID;

@RestController
@RequestMapping("/api/quizzes/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/{quizId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QuizAnalyticsResponse>> getAnalytics(
            @PathVariable UUID quizId) {
        log.info("GET /api/quizzes/analytics/{}", quizId);
        QuizAnalyticsResponse response = analyticsService.getQuizAnalytics(quizId);
        return ResponseEntity.ok(ApiResponse.success(response, "Analytics fetched"));
    }

    @PostMapping("/{quizId}/refresh")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> refreshAnalytics(@PathVariable UUID quizId) {
        log.info("POST /api/quizzes/analytics/{}/refresh", quizId);
        analyticsService.refreshAnalytics(quizId);
        return ResponseEntity.ok(ApiResponse.success(null, "Analytics refreshed"));
    }

    @DeleteMapping("/{quizId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAnalytics(@PathVariable UUID quizId) {
        log.info("DELETE /api/quizzes/analytics/{}", quizId);
        analyticsService.deleteAnalytics(quizId);
        return ResponseEntity.ok(ApiResponse.success(null, "Analytics deleted"));
    }
}