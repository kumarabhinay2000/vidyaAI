package org.vidyaai.aiservices.mapper.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.dto.response.LlmUsageResponse;
import org.vidyaai.aiservices.entity.LlmUsageLog;
import org.vidyaai.aiservices.enums.LlmProvider;
import org.vidyaai.aiservices.mapper.LlmUsageMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class LlmUsageMapperImpl implements LlmUsageMapper {

    @Override
    public LlmUsageLog toEntity(String serviceName, String operation, String provider,
                                String model, Integer inputTokens, Integer outputTokens,
                                Long responseTimeMs, boolean success, String errorMessage) {
        try {
            int in = inputTokens != null ? inputTokens : 0;
            int out = outputTokens != null ? outputTokens : 0;

            LlmProvider providerEnum;
            try {
                providerEnum = LlmProvider.fromCode(provider);
            } catch (Exception e) {
                providerEnum = LlmProvider.GROQ;
            }

            return LlmUsageLog.builder()
                    .serviceName(serviceName)
                    .operation(operation)
                    .llmProvider(providerEnum)
                    .modelName(model)
                    .inputTokens(in)
                    .outputTokens(out)
                    .totalTokens(in + out)
                    .responseTimeMs(responseTimeMs)
                    .success(success)
                    .errorMessage(errorMessage)
                    .cacheHit(false)
                    .build();
        } catch (Exception e) {
            log.error("Error mapping LlmUsageLog: {}", e.getMessage(), e);
            return LlmUsageLog.builder()
                    .serviceName(serviceName)
                    .operation(operation)
                    .success(success)
                    .build();
        }
    }

    @Override
    public LlmUsageResponse toResponse(LocalDateTime periodStart, LocalDateTime periodEnd,
                                       List<LlmUsageLog> logs) {
        try {
            long total = logs.size();
            long success = logs.stream().filter(l -> Boolean.TRUE.equals(l.getSuccess())).count();
            long failed = total - success;
            long cacheHits = logs.stream().filter(l -> Boolean.TRUE.equals(l.getCacheHit())).count();

            long totalTokens = logs.stream()
                    .filter(l -> Boolean.TRUE.equals(l.getSuccess()))
                    .mapToLong(l -> l.getTotalTokens() != null ? l.getTotalTokens() : 0)
                    .sum();

            BigDecimal totalCost = logs.stream()
                    .filter(l -> Boolean.TRUE.equals(l.getSuccess()) && l.getCost() != null)
                    .map(LlmUsageLog::getCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            double avgResponseTime = logs.stream()
                    .filter(l -> Boolean.TRUE.equals(l.getSuccess()) && l.getResponseTimeMs() != null)
                    .mapToLong(LlmUsageLog::getResponseTimeMs)
                    .average()
                    .orElse(0.0);

            Map<LlmProvider, Long> byProvider = new HashMap<>();
            logs.forEach(l -> {
                if (l.getLlmProvider() != null) {
                    byProvider.merge(l.getLlmProvider(), 1L, Long::sum);
                }
            });

            return LlmUsageResponse.builder()
                    .periodStart(periodStart)
                    .periodEnd(periodEnd)
                    .totalCalls(total)
                    .successfulCalls(success)
                    .failedCalls(failed)
                    .cacheHits(cacheHits)
                    .totalTokens(totalTokens)
                    .totalCost(totalCost)
                    .averageResponseTimeMs(avgResponseTime)
                    .callsByProvider(byProvider)
                    .build();
        } catch (Exception e) {
            log.error("Error building LlmUsageResponse: {}", e.getMessage(), e);
            return LlmUsageResponse.builder()
                    .periodStart(periodStart)
                    .periodEnd(periodEnd)
                    .totalCalls(0L)
                    .build();
        }
    }
}