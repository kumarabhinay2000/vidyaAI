package org.vidyaai.aiservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.aiservices.enums.LlmProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotResponse {

    private UUID chatId;
    private UUID videoId;
    private String sessionId;
    private String answer;
    private List<String> sources;
    private Double confidence;
    private LlmProvider llmProvider;
    private String modelName;
    private Integer tokensUsed;
    private Long responseTimeMs;
    private Boolean cached;
    private LocalDateTime createdAt;
}