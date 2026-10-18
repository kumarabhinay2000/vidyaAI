package org.vidyaai.videoservices.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vidyaai.videoservices.dto.response.ApiResponse;
import org.vidyaai.videoservices.storage.StorageService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/videos/health")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    private final StorageService storageService;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "video-service");
        status.put("timestamp", LocalDateTime.now());
        status.put("status", "UP");
        status.put("storageProvider", storageService.getProviderName());
        return ResponseEntity.ok(ApiResponse.success(status, "Video Service healthy"));
    }
}