package org.vidyaai.aiservices.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.aiservices.dto.request.ChatbotRequest;
import org.vidyaai.aiservices.dto.response.ApiResponse;
import org.vidyaai.aiservices.dto.response.ChatbotResponse;
import org.vidyaai.aiservices.security.SecurityUtils;
import org.vidyaai.aiservices.service.ChatbotService;

import java.util.UUID;

@RestController
@RequestMapping("/api/ai/chatbot")
@RequiredArgsConstructor
@Slf4j
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final SecurityUtils securityUtils;

    @PostMapping("/ask")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ChatbotResponse>> ask(
            @Valid @RequestBody ChatbotRequest request) {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("POST /api/ai/chatbot/ask userId={} videoId={}", userId, request.getVideoId());
        ChatbotResponse response = chatbotService.ask(request, userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Answer generated"));
    }

    @DeleteMapping("/history/{videoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteHistory(@PathVariable UUID videoId) {
        UUID userId = securityUtils.getCurrentUserId();
        log.info("DELETE /api/ai/chatbot/history/{} for userId={}", videoId, userId);
        chatbotService.deleteChatHistory(userId, videoId);
        return ResponseEntity.ok(ApiResponse.success(null, "Chat history deleted"));
    }
}