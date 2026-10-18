package org.vidyaai.videoservices.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.videoservices.dto.request.VideoSearchRequest;
import org.vidyaai.videoservices.dto.response.ApiResponse;
import org.vidyaai.videoservices.dto.response.PageResponse;
import org.vidyaai.videoservices.dto.response.VideoSummaryResponse;
import org.vidyaai.videoservices.security.SecurityUtils;
import org.vidyaai.videoservices.service.VideoService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos/search")
@RequiredArgsConstructor
@Slf4j
public class VideoSearchController {

    private final VideoService videoService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<VideoSummaryResponse>>> search(
            @Valid @RequestBody VideoSearchRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("POST /api/videos/search userId={} query={}", userId, request.getQuery());
        PageResponse<VideoSummaryResponse> response = videoService.search(request, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Search results"));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<VideoSummaryResponse>>> listMyVideos() {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("GET /api/videos/search/my userId={}", userId);
        List<VideoSummaryResponse> response = videoService.listUserVideos(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Videos fetched"));
    }
}