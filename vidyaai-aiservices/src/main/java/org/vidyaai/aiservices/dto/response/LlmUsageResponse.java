package org.vidyaai.aiservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vidyaai.aiservices.enums.LlmProvider;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmUsageResponse {

    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private Long totalCalls;
    private Long successfulCalls;
    private Long failedCalls;
    private Long cacheHits;
    private Long totalTokens;
    private BigDecimal totalCost;
    private Double averageResponseTimeMs;
    private Map<LlmProvider, Long> callsByProvider;
}