package org.vidyaai.aiservices.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.aiservices.dto.request.SummaryRequest;
import org.vidyaai.aiservices.dto.response.ApiResponse;
import org.vidyaai.aiservices.dto.response.SummaryResponse;
import org.vidyaai.aiservices.service.SummaryService;

import java.util.UUID;

@RestController
@RequestMapping("/api/ai/summary")
@RequiredArgsConstructor
@Slf4j
public class SummaryController {

    private final SummaryService summaryService;

    @PostMapping("/generate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SummaryResponse>> generate(
            @Valid @RequestBody SummaryRequest request) {
        log.info("POST /api/ai/summary/generate for videoId={}", request.getVideoId());
        SummaryResponse response = summaryService.generateSummary(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Summary generated"));
    }

    @GetMapping("/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SummaryResponse>> getSummary(@PathVariable UUID videoId) {
        log.info("GET /api/ai/summary/{}", videoId);
        SummaryResponse response = summaryService.getSummary(videoId);
        return ResponseEntity.ok(ApiResponse.success(response, "Summary fetched"));
    }

    @DeleteMapping("/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteSummary(@PathVariable UUID videoId) {
        log.info("DELETE /api/ai/summary/{}", videoId);
        summaryService.deleteSummary(videoId);
        return ResponseEntity.ok(ApiResponse.success(null, "Summary deleted"));
    }
}