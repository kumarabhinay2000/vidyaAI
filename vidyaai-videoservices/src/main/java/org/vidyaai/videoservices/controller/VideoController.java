package org.vidyaai.videoservices.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.vidyaai.videoservices.dto.request.UpdateVideoRequest;
import org.vidyaai.videoservices.dto.request.UploadVideoRequest;
import org.vidyaai.videoservices.dto.response.ApiResponse;
import org.vidyaai.videoservices.dto.response.VideoResponse;
import org.vidyaai.videoservices.dto.response.VideoUploadResponse;
import org.vidyaai.videoservices.security.SecurityUtils;
import org.vidyaai.videoservices.service.VideoService;

import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

    private final VideoService videoService;
    private final SecurityUtils securityUtils;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<VideoUploadResponse>> uploadVideo(
            @RequestPart("metadata") @Valid UploadVideoRequest request,
            @RequestPart("file") MultipartFile file) {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("POST /api/videos/upload userId={} filename={}",
                userId, file.getOriginalFilename());
        VideoUploadResponse response = videoService.uploadVideo(request, file, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Video uploaded"));
    }

    @GetMapping("/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<VideoResponse>> getVideo(@PathVariable UUID videoId) {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("GET /api/videos/{} userId={}", videoId, userId);
        VideoResponse response = videoService.getVideo(videoId, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Video fetched"));
    }

    @PutMapping("/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<VideoResponse>> updateVideo(
            @PathVariable UUID videoId,
            @Valid @RequestBody UpdateVideoRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("PUT /api/videos/{} userId={}", videoId, userId);
        VideoResponse response = videoService.updateVideo(videoId, request, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Video updated"));
    }

    @DeleteMapping("/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteVideo(@PathVariable UUID videoId) {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("DELETE /api/videos/{} userId={}", videoId, userId);
        videoService.deleteVideo(videoId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Video deleted"));
    }

    @PostMapping("/{videoId}/view")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> incrementView(@PathVariable UUID videoId) {
        log.info("POST /api/videos/{}/view", videoId);
        videoService.incrementView(videoId);
        return ResponseEntity.ok(ApiResponse.success(null, "View recorded"));
    }

    @GetMapping("/{videoId}/stream")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InputStreamResource> streamVideo(@PathVariable UUID videoId) {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("GET /api/videos/{}/stream userId={}", videoId, userId);
        InputStream stream = videoService.streamVideo(videoId, userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(new InputStreamResource(stream));
    }
}