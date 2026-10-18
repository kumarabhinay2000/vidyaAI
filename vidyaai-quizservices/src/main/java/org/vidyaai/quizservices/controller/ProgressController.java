package org.vidyaai.quizservices.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.quizservices.dto.response.ApiResponse;
import org.vidyaai.quizservices.dto.response.LearningProgressResponse;
import org.vidyaai.quizservices.security.SecurityUtils;
import org.vidyaai.quizservices.service.ProgressService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quizzes/progress")
@RequiredArgsConstructor
@Slf4j
public class ProgressController {

    private final ProgressService progressService;
    private final SecurityUtils securityUtils;

    @GetMapping("/video/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LearningProgressResponse>> getProgress(
            @PathVariable UUID videoId) {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("GET /api/quizzes/progress/video/{} for userId={}", videoId, userId);
        LearningProgressResponse response = progressService.getProgress(userId, videoId);
        return ResponseEntity.ok(ApiResponse.success(response, "Progress fetched"));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<LearningProgressResponse>>> listMyProgress() {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("GET /api/quizzes/progress/my for userId={}", userId);
        List<LearningProgressResponse> response = progressService.getUserProgress(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Progress list fetched"));
    }
}