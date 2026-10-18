package org.vidyaai.videoservices.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.videoservices.dto.response.ApiResponse;
import org.vidyaai.videoservices.dto.response.VideoStatusResponse;
import org.vidyaai.videoservices.security.SecurityUtils;
import org.vidyaai.videoservices.service.VideoService;

import java.util.UUID;

@RestController
@RequestMapping("/api/videos/status")
@RequiredArgsConstructor
@Slf4j
public class VideoStatusController {

    private final VideoService videoService;
    private final SecurityUtils securityUtils;

    @GetMapping("/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<VideoStatusResponse>> getStatus(@PathVariable UUID videoId) {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("GET /api/videos/status/{} userId={}", videoId, userId);
        VideoStatusResponse response = videoService.getStatus(videoId, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Status fetched"));
    }
}