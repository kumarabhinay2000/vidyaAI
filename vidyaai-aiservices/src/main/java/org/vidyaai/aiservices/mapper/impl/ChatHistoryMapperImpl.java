package org.vidyaai.aiservices.mapper.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.dto.response.ChatbotResponse;
import org.vidyaai.aiservices.entity.AiChatHistory;
import org.vidyaai.aiservices.mapper.ChatHistoryMapper;

import java.util.Collections;

@Component
@Slf4j
public class ChatHistoryMapperImpl implements ChatHistoryMapper {

    @Override
    public ChatbotResponse toChatbotResponse(AiChatHistory entity) {
        try {
            return ChatbotResponse.builder()
                    .chatId(entity.getId())
                    .videoId(entity.getVideoId())
                    .sessionId(entity.getSessionId())
                    .answer(entity.getAnswer())
                    .sources(Collections.emptyList())
                    .confidence(null)
                    .tokensUsed(entity.getTokensUsed())
                    .responseTimeMs(entity.getResponseTimeMs())
                    .createdAt(entity.getCreatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error mapping AiChatHistory: {}", e.getMessage(), e);
            return ChatbotResponse.builder()
                    .chatId(entity.getId())
                    .videoId(entity.getVideoId())
                    .answer(entity.getAnswer())
                    .build();
        }
    }
}