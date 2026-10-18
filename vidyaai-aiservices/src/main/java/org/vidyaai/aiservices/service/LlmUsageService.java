package org.vidyaai.aiservices.service;

import org.vidyaai.aiservices.dto.response.LlmUsageResponse;

public interface LlmUsageService {

    LlmUsageResponse getUsageStats(int days);

    long countCacheHits(int days);

    long countFailures(int days);

    Long sumTokens(int days);

    Double averageResponseTime(int days);
}