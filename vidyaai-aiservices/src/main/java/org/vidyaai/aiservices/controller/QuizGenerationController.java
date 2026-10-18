package org.vidyaai.aiservices.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.aiservices.dto.request.QuizGenerationRequest;
import org.vidyaai.aiservices.dto.response.ApiResponse;
import org.vidyaai.aiservices.dto.response.QuizGenerationResponse;
import org.vidyaai.aiservices.service.QuizGenerationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/ai/quiz")
@RequiredArgsConstructor
@Slf4j
public class QuizGenerationController {

    private final QuizGenerationService quizGenerationService;

    @PostMapping("/generate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QuizGenerationResponse>> generate(
            @Valid @RequestBody QuizGenerationRequest request) {
        log.info("POST /api/ai/quiz/generate for videoId={}", request.getVideoId());
        QuizGenerationResponse response = quizGenerationService.generateQuiz(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Quiz generated"));
    }

    @GetMapping("/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QuizGenerationResponse>> getQuiz(@PathVariable UUID videoId) {
        log.info("GET /api/ai/quiz/{}", videoId);
        QuizGenerationResponse response = quizGenerationService.getQuiz(videoId);
        return ResponseEntity.ok(ApiResponse.success(response, "Quiz fetched"));
    }

    @DeleteMapping("/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(@PathVariable UUID videoId) {
        log.info("DELETE /api/ai/quiz/{}", videoId);
        quizGenerationService.deleteQuiz(videoId);
        return ResponseEntity.ok(ApiResponse.success(null, "Quiz deleted"));
    }
}