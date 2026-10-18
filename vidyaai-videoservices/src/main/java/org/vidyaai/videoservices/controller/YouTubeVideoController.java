package org.vidyaai.videoservices.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.videoservices.dto.request.YouTubeVideoRequest;
import org.vidyaai.videoservices.dto.response.ApiResponse;
import org.vidyaai.videoservices.dto.response.VideoUploadResponse;
import org.vidyaai.videoservices.security.SecurityUtils;
import org.vidyaai.videoservices.service.VideoService;

import java.util.UUID;

@RestController
@RequestMapping("/api/videos/youtube")
@RequiredArgsConstructor
@Slf4j
public class YouTubeVideoController {

    private final VideoService videoService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<VideoUploadResponse>> registerYouTubeVideo(
            @Valid @RequestBody YouTubeVideoRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("POST /api/videos/youtube userId={} url={}", userId, request.getYoutubeUrl());
        VideoUploadResponse response = videoService.registerYouTubeVideo(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "YouTube video registered"));
    }
}