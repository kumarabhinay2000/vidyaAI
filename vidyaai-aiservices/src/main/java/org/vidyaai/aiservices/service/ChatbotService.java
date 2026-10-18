package org.vidyaai.aiservices.service;

import org.vidyaai.aiservices.dto.request.ChatbotRequest;
import org.vidyaai.aiservices.dto.response.ChatbotResponse;

import java.util.UUID;

public interface ChatbotService {

    ChatbotResponse ask(ChatbotRequest request, UUID userId);

    void deleteChatHistory(UUID userId, UUID videoId);

    long countUserMessages(UUID userId);
}