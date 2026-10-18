package org.vidyaai.quizservices.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vidyaai.quizservices.dto.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes/health")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "quiz-service");
        status.put("timestamp", LocalDateTime.now());
        status.put("status", "UP");
        return ResponseEntity.ok(ApiResponse.success(status, "Quiz Service healthy"));
    }
}