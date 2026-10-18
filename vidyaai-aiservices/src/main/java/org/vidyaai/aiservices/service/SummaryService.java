package org.vidyaai.aiservices.service;

import org.vidyaai.aiservices.dto.request.SummaryRequest;
import org.vidyaai.aiservices.dto.response.SummaryResponse;

import java.util.UUID;

public interface SummaryService {

    SummaryResponse generateSummary(SummaryRequest request);

    SummaryResponse getSummary(UUID videoId);

    void deleteSummary(UUID videoId);
}