package org.vidyaai.aiservices.mapper;

import org.vidyaai.aiservices.dto.response.ChatbotResponse;
import org.vidyaai.aiservices.entity.AiChatHistory;

public interface ChatHistoryMapper {

    ChatbotResponse toChatbotResponse(AiChatHistory entity);
}
