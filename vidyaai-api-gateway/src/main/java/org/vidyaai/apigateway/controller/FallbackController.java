package org.vidyaai.apigateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    @GetMapping("/{service}")
    public ResponseEntity<Map<String, Object>> fallbackGet(@PathVariable String service) {
        log.warn("Fallback triggered for service={} (GET)", service);
        return buildResponse(service);
    }

    @PostMapping("/{service}")
    public ResponseEntity<Map<String, Object>> fallbackPost(@PathVariable String service) {
        log.warn("Fallback triggered for service={} (POST)", service);
        return buildResponse(service);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(String service) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("error", "SERVICE_UNAVAILABLE");
        body.put("errorCode", "GW_005");
        body.put("message", "Service '" + service + "' is temporarily unavailable. Please try again shortly.");
        body.put("service", service);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}