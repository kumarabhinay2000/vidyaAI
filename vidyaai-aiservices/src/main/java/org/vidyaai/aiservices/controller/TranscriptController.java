package org.vidyaai.aiservices.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.aiservices.dto.request.TranscriptRequest;
import org.vidyaai.aiservices.dto.response.ApiResponse;
import org.vidyaai.aiservices.dto.response.TranscriptResponse;
import org.vidyaai.aiservices.service.TranscriptService;

import java.util.UUID;

@RestController
@RequestMapping("/api/ai/transcript")
@RequiredArgsConstructor
@Slf4j
public class TranscriptController {

    private final TranscriptService transcriptService;

    @PostMapping("/extract")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TranscriptResponse>> extract(
            @Valid @RequestBody TranscriptRequest request) {
        log.info("POST /api/ai/transcript/extract for videoId={}", request.getVideoId());
        TranscriptResponse response = transcriptService.extractTranscript(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Transcript extracted"));
    }

    @GetMapping("/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TranscriptResponse>> get(@PathVariable UUID videoId) {
        log.info("GET /api/ai/transcript/{}", videoId);
        TranscriptResponse response = transcriptService.getTranscript(videoId);
        return ResponseEntity.ok(ApiResponse.success(response, "Transcript fetched"));
    }

    @DeleteMapping("/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID videoId) {
        log.info("DELETE /api/ai/transcript/{}", videoId);
        transcriptService.deleteTranscript(videoId);
        return ResponseEntity.ok(ApiResponse.success(null, "Transcript deleted"));
    }
}