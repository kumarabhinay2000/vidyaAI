package org.vidyaai.aiservices.mapper;

import org.vidyaai.aiservices.dto.response.LlmUsageResponse;
import org.vidyaai.aiservices.entity.LlmUsageLog;

import java.time.LocalDateTime;
import java.util.List;

public interface LlmUsageMapper {

    LlmUsageLog toEntity(String serviceName, String operation, String provider,
                         String model, Integer inputTokens, Integer outputTokens,
                         Long responseTimeMs, boolean success, String errorMessage);

    LlmUsageResponse toResponse(LocalDateTime periodStart, LocalDateTime periodEnd,
                                List<LlmUsageLog> logs);
}