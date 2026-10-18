package org.vidyaai.quizservices.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.quizservices.constant.CacheKeys;
import org.vidyaai.quizservices.constant.RedisTTL;
import org.vidyaai.quizservices.dto.response.QuizAnalyticsResponse;
import org.vidyaai.quizservices.entity.QuizAnalytics;
import org.vidyaai.quizservices.exception.QuizServiceException;
import org.vidyaai.quizservices.mapper.AnalyticsMapper;
import org.vidyaai.quizservices.repository.QuizAnalyticsRepository;
import org.vidyaai.quizservices.repository.QuizAttemptRepository;
import org.vidyaai.quizservices.service.AnalyticsService;
import org.vidyaai.quizservices.service.CacheService;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final QuizAnalyticsRepository quizAnalyticsRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final AnalyticsMapper analyticsMapper;
    private final CacheService cacheService;

    @Override
    @Transactional(readOnly = true)
    public QuizAnalyticsResponse getQuizAnalytics(UUID quizId) {
        try {
            String key = CacheKeys.analytics(quizId.toString());
            Optional<QuizAnalyticsResponse> cached = cacheService.get(key, QuizAnalyticsResponse.class);
            if (cached.isPresent()) {
                return cached.get();
            }

            QuizAnalytics analytics = quizAnalyticsRepository.findByQuizId(quizId)
                    .orElseGet(() -> refreshAndFetch(quizId));

            QuizAnalyticsResponse response = analyticsMapper.toResponse(analytics);
            cacheService.put(key, response, Duration.ofSeconds(RedisTTL.ANALYTICS_SECONDS));
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch analytics for quiz {}: {}", quizId, e.getMessage(), e);
            throw new QuizServiceException("Failed to fetch analytics", e);
        }
    }

    @Override
    @Transactional
    public void refreshAnalytics(UUID quizId) {
        try {
            refreshAndFetch(quizId);
            cacheService.delete(CacheKeys.analytics(quizId.toString()));
            log.info("Analytics refreshed for quiz {}", quizId);
        } catch (Exception e) {
            log.error("Failed to refresh analytics for quiz {}: {}", quizId, e.getMessage(), e);
            throw new QuizServiceException("Failed to refresh analytics", e);
        }
    }

    @Override
    @Transactional
    public void deleteAnalytics(UUID quizId) {
        try {
            quizAnalyticsRepository.deleteByQuizId(quizId);
            cacheService.delete(CacheKeys.analytics(quizId.toString()));
            log.info("Analytics deleted for quiz {}", quizId);
        } catch (Exception e) {
            log.error("Failed to delete analytics for quiz {}: {}", quizId, e.getMessage(), e);
            throw new QuizServiceException("Failed to delete analytics", e);
        }
    }

    private QuizAnalytics refreshAndFetch(UUID quizId) {
        long totalAttempts = quizAttemptRepository.countCompletedByQuiz(quizId);
        Double avg = quizAttemptRepository.avgPercentageByQuiz(quizId);
        Integer max = quizAttemptRepository.maxScoreByQuiz(quizId);
        Integer min = quizAttemptRepository.minScoreByQuiz(quizId);
        long passed = quizAttemptRepository.countPassedByQuiz(quizId);
        Double avgTime = quizAttemptRepository.avgTimeTakenByQuiz(quizId);

        double passRate = totalAttempts > 0 ? (passed * 100.0) / totalAttempts : 0.0;

        QuizAnalytics analytics = quizAnalyticsRepository.findByQuizId(quizId)
                .orElseGet(() -> QuizAnalytics.builder().quizId(quizId).build());

        analytics.setTotalAttempts((int) totalAttempts);
        analytics.setAvgScore(avg != null ? avg : 0.0);
        analytics.setHighestScore(max != null ? max : 0);
        analytics.setLowestScore(min != null ? min : 0);
        analytics.setPassRate(passRate);
        analytics.setAvgTimeTaken(avgTime != null ? avgTime.intValue() : 0);

        return quizAnalyticsRepository.save(analytics);
    }
}