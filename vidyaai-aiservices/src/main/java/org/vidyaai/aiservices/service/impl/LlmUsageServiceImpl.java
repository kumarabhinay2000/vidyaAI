package org.vidyaai.aiservices.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.aiservices.dto.response.LlmUsageResponse;
import org.vidyaai.aiservices.entity.LlmUsageLog;
import org.vidyaai.aiservices.enums.LlmProvider;
import org.vidyaai.aiservices.mapper.LlmUsageMapper;
import org.vidyaai.aiservices.repository.LlmUsageLogRepository;
import org.vidyaai.aiservices.service.LlmUsageService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LlmUsageServiceImpl implements LlmUsageService {

    private final LlmUsageLogRepository llmUsageLogRepository;
    private final LlmUsageMapper llmUsageMapper;

    @Override
    @Transactional(readOnly = true)
    public LlmUsageResponse getUsageStats(int days) {
        try {
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusDays(days);
            List<LlmUsageLog> logs = llmUsageLogRepository
                    .findByLlmProviderAndCreatedAtBetween(LlmProvider.GROQ, start, end);
            return llmUsageMapper.toResponse(start, end, logs);
        } catch (Exception e) {
            log.error("Error getting usage stats: {}", e.getMessage(), e);
            LocalDateTime end = LocalDateTime.now();
            return llmUsageMapper.toResponse(end.minusDays(days), end, List.of());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countCacheHits(int days) {
        try {
            return llmUsageLogRepository.countByCacheHitTrueAndCreatedAtAfter(
                    LocalDateTime.now().minusDays(days));
        } catch (Exception e) {
            log.error("Error counting cache hits: {}", e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countFailures(int days) {
        try {
            return llmUsageLogRepository.countBySuccessFalseAndCreatedAtAfter(
                    LocalDateTime.now().minusDays(days));
        } catch (Exception e) {
            log.error("Error counting failures: {}", e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long sumTokens(int days) {
        try {
            Long total = llmUsageLogRepository.sumTotalTokensSince(
                    LocalDateTime.now().minusDays(days));
            return total != null ? total : 0L;
        } catch (Exception e) {
            log.error("Error summing tokens: {}", e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Double averageResponseTime(int days) {
        try {
            Double avg = llmUsageLogRepository.avgResponseTimeSince(
                    LocalDateTime.now().minusDays(days));
            return avg != null ? avg : 0.0;
        } catch (Exception e) {
            log.error("Error computing average response time: {}", e.getMessage(), e);
            return 0.0;
        }
    }
}