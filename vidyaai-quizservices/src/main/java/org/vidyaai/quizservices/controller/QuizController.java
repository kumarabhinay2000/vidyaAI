package org.vidyaai.quizservices.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.quizservices.dto.request.CreateQuizRequest;
import org.vidyaai.quizservices.dto.request.UpdateQuizRequest;
import org.vidyaai.quizservices.dto.response.ApiResponse;
import org.vidyaai.quizservices.dto.response.QuizResponse;
import org.vidyaai.quizservices.security.SecurityUtils;
import org.vidyaai.quizservices.service.QuizService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final QuizService quizService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QuizResponse>> createQuiz(
            @Valid @RequestBody CreateQuizRequest request) {
        log.info("POST /api/quizzes for videoId={}", request.getVideoId());
        QuizResponse response = quizService.createQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Quiz created"));
    }

    @GetMapping("/{quizId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuiz(@PathVariable UUID quizId) {
        log.info("GET /api/quizzes/{}", quizId);
        QuizResponse response = quizService.getQuiz(quizId);
        return ResponseEntity.ok(ApiResponse.success(response, "Quiz fetched"));
    }

    @GetMapping("/{quizId}/take")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuizForTaking(@PathVariable UUID quizId) {
        log.info("GET /api/quizzes/{}/take", quizId);
        QuizResponse response = quizService.getQuizForTaking(quizId);
        return ResponseEntity.ok(ApiResponse.success(response, "Quiz fetched for taking"));
    }

    @GetMapping("/video/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuizByVideo(@PathVariable UUID videoId) {
        log.info("GET /api/quizzes/video/{}", videoId);
        QuizResponse response = quizService.getQuizByVideoId(videoId);
        return ResponseEntity.ok(ApiResponse.success(response, "Quiz fetched by video"));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<QuizResponse>>> listMyQuizzes() {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("GET /api/quizzes/my for userId={}", userId);
        List<QuizResponse> response = quizService.listUserQuizzes(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Quizzes fetched"));
    }

    @PutMapping("/{quizId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QuizResponse>> updateQuiz(
            @PathVariable UUID quizId,
            @Valid @RequestBody UpdateQuizRequest request) {
        log.info("PUT /api/quizzes/{}", quizId);
        QuizResponse response = quizService.updateQuiz(quizId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Quiz updated"));
    }

    @DeleteMapping("/{quizId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(@PathVariable UUID quizId) {
        log.info("DELETE /api/quizzes/{}", quizId);
        quizService.deleteQuiz(quizId);
        return ResponseEntity.ok(ApiResponse.success(null, "Quiz deleted"));
    }
}