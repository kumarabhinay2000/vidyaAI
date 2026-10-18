package org.vidyaai.quizservices.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.quizservices.dto.request.StartAttemptRequest;
import org.vidyaai.quizservices.dto.request.SubmitQuizRequest;
import org.vidyaai.quizservices.dto.response.ApiResponse;
import org.vidyaai.quizservices.dto.response.AttemptResultResponse;
import org.vidyaai.quizservices.dto.response.QuizAttemptResponse;
import org.vidyaai.quizservices.security.SecurityUtils;
import org.vidyaai.quizservices.service.AttemptService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quizzes/attempts")
@RequiredArgsConstructor
@Slf4j
public class AttemptController {

    private final AttemptService attemptService;
    private final SecurityUtils securityUtils;

    @PostMapping("/start")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QuizAttemptResponse>> startAttempt(
            @Valid @RequestBody StartAttemptRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("POST /api/quizzes/attempts/start for quizId={} userId={}",
                request.getQuizId(), userId);
        QuizAttemptResponse response = attemptService.startAttempt(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Attempt started"));
    }

    @PostMapping("/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AttemptResultResponse>> submitAttempt(
            @Valid @RequestBody SubmitQuizRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("POST /api/quizzes/attempts/submit attemptId={} userId={}",
                request.getAttemptId(), userId);
        AttemptResultResponse response = attemptService.submitAttempt(request, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Attempt submitted"));
    }

    @GetMapping("/{attemptId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QuizAttemptResponse>> getAttempt(
            @PathVariable UUID attemptId) {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("GET /api/quizzes/attempts/{} for userId={}", attemptId, userId);
        QuizAttemptResponse response = attemptService.getAttempt(attemptId, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Attempt fetched"));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<QuizAttemptResponse>>> listMyAttempts() {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("GET /api/quizzes/attempts/my for userId={}", userId);
        List<QuizAttemptResponse> response = attemptService.getUserAttempts(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Attempts fetched"));
    }

    @GetMapping("/quiz/{quizId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<QuizAttemptResponse>>> listQuizAttempts(
            @PathVariable UUID quizId) {
        log.info("GET /api/quizzes/attempts/quiz/{}", quizId);
        List<QuizAttemptResponse> response = attemptService.getQuizAttempts(quizId);
        return ResponseEntity.ok(ApiResponse.success(response, "Attempts fetched"));
    }
}