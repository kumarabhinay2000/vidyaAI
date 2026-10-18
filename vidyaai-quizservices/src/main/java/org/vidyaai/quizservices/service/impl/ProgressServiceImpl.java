package org.vidyaai.quizservices.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.quizservices.dto.response.LearningProgressResponse;
import org.vidyaai.quizservices.entity.LearningProgress;
import org.vidyaai.quizservices.entity.QuizAttempt;
import org.vidyaai.quizservices.enums.ProgressStatus;
import org.vidyaai.quizservices.exception.ResourceNotFoundException;
import org.vidyaai.quizservices.exception.QuizServiceException;
import org.vidyaai.quizservices.mapper.ProgressMapper;
import org.vidyaai.quizservices.repository.LearningProgressRepository;
import org.vidyaai.quizservices.service.ProgressService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressServiceImpl implements ProgressService {

    private final LearningProgressRepository learningProgressRepository;
    private final ProgressMapper progressMapper;

    @Override
    @Transactional
    public void updateProgress(UUID userId, UUID videoId, UUID quizId, QuizAttempt attempt) {
        try {
            LearningProgress progress = learningProgressRepository
                    .findByUserIdAndVideoId(userId, videoId)
                    .orElseGet(() -> LearningProgress.builder()
                            .userId(userId)
                            .videoId(videoId)
                            .quizId(quizId)
                            .status(ProgressStatus.NOT_STARTED)
                            .build());

            progress.start();
            progress.setQuizId(quizId);
            progress.incrementAttempts();
            progress.updateBestScore(attempt.getScore() != null ? attempt.getScore() : 0);

            if (Boolean.TRUE.equals(attempt.getIsPassed())) {
                progress.complete();
            } else {
                int pct = attempt.getPercentage() != null
                        ? (int) Math.round(attempt.getPercentage()) : 0;
                progress.setProgressPercentage(Math.min(99, pct));
            }

            learningProgressRepository.save(progress);
            log.debug("Learning progress updated for user={} video={}", userId, videoId);
        } catch (Exception e) {
            log.error("Failed to update progress: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LearningProgressResponse getProgress(UUID userId, UUID videoId) {
        try {
            LearningProgress progress = learningProgressRepository
                    .findByUserIdAndVideoId(userId, videoId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "LearningProgress", "userId+videoId", userId + "+" + videoId));
            return progressMapper.toResponse(progress);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch progress: {}", e.getMessage(), e);
            throw new QuizServiceException("Failed to fetch progress", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningProgressResponse> getUserProgress(UUID userId) {
        try {
            return learningProgressRepository.findByUserIdOrderByLastAccessedDesc(userId)
                    .stream().map(progressMapper::toResponse).toList();
        } catch (Exception e) {
            log.error("Failed to fetch user progress: {}", e.getMessage(), e);
            throw new QuizServiceException("Failed to fetch user progress", e);
        }
    }
}