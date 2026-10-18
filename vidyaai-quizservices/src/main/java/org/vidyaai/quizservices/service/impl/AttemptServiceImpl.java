package org.vidyaai.quizservices.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.quizservices.constant.CacheKeys;
import org.vidyaai.quizservices.constant.RedisTTL;
import org.vidyaai.quizservices.dto.request.StartAttemptRequest;
import org.vidyaai.quizservices.dto.request.SubmitQuizRequest;
import org.vidyaai.quizservices.dto.response.AttemptResultResponse;
import org.vidyaai.quizservices.dto.response.QuizAttemptResponse;
import org.vidyaai.quizservices.entity.Quiz;
import org.vidyaai.quizservices.entity.QuizAttempt;
import org.vidyaai.quizservices.entity.QuizQuestion;
import org.vidyaai.quizservices.enums.AttemptStatus;
import org.vidyaai.quizservices.exception.AccessDeniedException;
import org.vidyaai.quizservices.exception.AttemptCooldownException;
import org.vidyaai.quizservices.exception.AttemptNotFoundException;
import org.vidyaai.quizservices.exception.MaxAttemptsReachedException;
import org.vidyaai.quizservices.exception.QuizNotFoundException;
import org.vidyaai.quizservices.exception.QuizServiceException;
import org.vidyaai.quizservices.mapper.AttemptMapper;
import org.vidyaai.quizservices.repository.QuizAttemptRepository;
import org.vidyaai.quizservices.repository.QuizQuestionRepository;
import org.vidyaai.quizservices.repository.QuizRepository;
import org.vidyaai.quizservices.service.AttemptService;
import org.vidyaai.quizservices.service.CacheService;
import org.vidyaai.quizservices.service.LeaderboardService;
import org.vidyaai.quizservices.service.ProgressService;
import org.vidyaai.quizservices.service.QuizEventProducer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttemptServiceImpl implements AttemptService {

    private static final int MAX_ATTEMPTS = 10;
    private static final long COOLDOWN_MINUTES = 5L;

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final AttemptMapper attemptMapper;
    private final CacheService cacheService;
    private final LeaderboardService leaderboardService;
    private final ProgressService progressService;
    private final QuizEventProducer quizEventProducer;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public QuizAttemptResponse startAttempt(StartAttemptRequest request, UUID userId) {
        log.info("Starting attempt: quizId={} userId={}", request.getQuizId(), userId);
        try {
            Quiz quiz = quizRepository.findByIdAndIsDeletedFalse(request.getQuizId())
                    .orElseThrow(() -> new QuizNotFoundException(request.getQuizId()));

            if (!quiz.isPublished()) {
                throw new QuizNotFoundException("Quiz is not published: " + request.getQuizId());
            }

            // Max attempts check
            long attemptCount = quizAttemptRepository.countByQuizIdAndUserId(quiz.getId(), userId);
            if (attemptCount >= MAX_ATTEMPTS) {
                throw new MaxAttemptsReachedException(quiz.getId(), userId, attemptCount);
            }

            // Cooldown check (based on last attempt time)
            List<QuizAttempt> recent = quizAttemptRepository
                    .findByQuizIdAndUserIdOrderByCreatedAtDesc(quiz.getId(), userId);
            if (!recent.isEmpty()) {
                QuizAttempt last = recent.get(0);
                if (last.getCreatedAt() != null
                        && last.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(COOLDOWN_MINUTES))) {
                    throw new AttemptCooldownException(quiz.getId(), userId, COOLDOWN_MINUTES);
                }
            }

            int totalMarks = quizQuestionRepository.findByQuizIdOrderByQuestionOrderAsc(quiz.getId())
                    .stream().mapToInt(QuizQuestion::getMarks).sum();

            QuizAttempt attempt = QuizAttempt.builder()
                    .quizId(quiz.getId())
                    .userId(userId)
                    .status(AttemptStatus.IN_PROGRESS)
                    .startedAt(LocalDateTime.now())
                    .totalMarks(totalMarks)
                    .score(0)
                    .correctAnswers(0)
                    .wrongAnswers(0)
                    .unanswered(0)
                    .build();

            QuizAttempt saved = quizAttemptRepository.save(attempt);
            log.info("Attempt started: attemptId={} quizId={} userId={}",
                    saved.getId(), saved.getQuizId(), saved.getUserId());
            return attemptMapper.toResponse(saved);
        } catch (QuizNotFoundException | MaxAttemptsReachedException | AttemptCooldownException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to start attempt: {}", e.getMessage(), e);
            throw new QuizServiceException("Failed to start attempt", e);
        }
    }

    @Override
    @Transactional
    public AttemptResultResponse submitAttempt(SubmitQuizRequest request, UUID userId) {
        log.info("Submitting attempt: attemptId={} quizId={} userId={}",
                request.getAttemptId(), request.getQuizId(), userId);
        try {
            QuizAttempt attempt = quizAttemptRepository.findByIdAndUserId(
                            request.getAttemptId(), userId)
                    .orElseThrow(() -> new AttemptNotFoundException(request.getAttemptId()));

            if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
                throw new QuizServiceException("Attempt is not in progress: " + attempt.getId());
            }

            Quiz quiz = quizRepository.findByIdAndIsDeletedFalse(request.getQuizId())
                    .orElseThrow(() -> new QuizNotFoundException(request.getQuizId()));

            List<QuizQuestion> questions = quizQuestionRepository
                    .findByQuizIdOrderByQuestionOrderAsc(quiz.getId());

            Map<UUID, String> answers = request.getAnswers() != null
                    ? request.getAnswers() : new HashMap<>();

            int score = 0;
            int correct = 0;
            int wrong = 0;
            int unanswered = 0;
            int totalMarks = 0;

            List<AttemptResultResponse.AnswerReview> reviews = new ArrayList<>();

            for (QuizQuestion question : questions) {
                totalMarks += question.getMarks();
                String givenAnswer = answers.get(question.getId());
                boolean isCorrect = givenAnswer != null && question.isCorrect(givenAnswer);

                if (givenAnswer == null || givenAnswer.isBlank()) {
                    unanswered++;
                } else if (isCorrect) {
                    correct++;
                    score += question.getMarks();
                } else {
                    wrong++;
                }

                reviews.add(AttemptResultResponse.AnswerReview.builder()
                        .questionId(question.getId())
                        .questionText(question.getQuestionText())
                        .givenAnswer(givenAnswer)
                        .correctAnswer(question.getCorrectAnswer())
                        .isCorrect(isCorrect)
                        .explanation(question.getExplanation())
                        .marksAwarded(isCorrect ? question.getMarks() : 0)
                        .build());
            }

            attempt.setScore(score);
            attempt.setTotalMarks(totalMarks);
            attempt.setCorrectAnswers(correct);
            attempt.setWrongAnswers(wrong);
            attempt.setUnanswered(unanswered);
            attempt.setAnswers(serializeAnswers(answers));
            attempt.setTimeTakenSeconds(request.getTimeTakenSeconds() != null
                    ? request.getTimeTakenSeconds()
                    : (int) Duration.between(attempt.getStartedAt(), LocalDateTime.now()).toSeconds());
            attempt.calculatePercentage();

            double passThreshold = quiz.getPassingScore() != null
                    ? quiz.getPassingScore()
                    : 70;
            boolean passed = attempt.getPercentage() != null && attempt.getPercentage() >= passThreshold;
            attempt.setIsPassed(passed);
            attempt.markCompleted();

            QuizAttempt saved = quizAttemptRepository.save(attempt);

            // Update leaderboard
            try {
                leaderboardService.recordAttempt(userId, quiz.getId(), saved);
            } catch (Exception e) {
                log.error("Failed to update leaderboard for attempt {}: {}",
                        saved.getId(), e.getMessage());
            }

            // Update learning progress
            try {
                progressService.updateProgress(userId, quiz.getVideoId(), quiz.getId(), saved);
            } catch (Exception e) {
                log.error("Failed to update learning progress for attempt {}: {}",
                        saved.getId(), e.getMessage());
            }

            // Publish Kafka event
            try {
                quizEventProducer.publishQuizAttempted(saved, quiz.getVideoId());
                if (passed) {
                    quizEventProducer.publishQuizCompleted(saved, quiz.getVideoId());
                }
            } catch (Exception e) {
                log.error("Failed to publish quiz events for attempt {}: {}",
                        saved.getId(), e.getMessage());
            }

            // Invalidate analytics cache
            cacheService.delete(CacheKeys.analytics(quiz.getId().toString()));
            cacheService.delete(CacheKeys.userStats(userId.toString()));

            log.info("Attempt submitted: attemptId={} score={}/{} passed={}",
                    saved.getId(), score, totalMarks, passed);

            return attemptMapper.toResultResponse(saved, reviews);
        } catch (QuizServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to submit attempt: {}", e.getMessage(), e);
            throw new QuizServiceException("Failed to submit attempt", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QuizAttemptResponse getAttempt(UUID attemptId, UUID userId) {
        try {
            QuizAttempt attempt = quizAttemptRepository.findByIdAndUserId(attemptId, userId)
                    .orElseThrow(() -> new AttemptNotFoundException(attemptId));
            return attemptMapper.toResponse(attempt);
        } catch (AttemptNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch attempt {}: {}", attemptId, e.getMessage(), e);
            throw new QuizServiceException("Failed to fetch attempt", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttemptResponse> getUserAttempts(UUID userId) {
        try {
            return quizAttemptRepository.findByUserIdOrderByCreatedAtDesc(userId)
                    .stream().map(attemptMapper::toResponse).toList();
        } catch (Exception e) {
            log.error("Failed to fetch attempts for user {}: {}", userId, e.getMessage(), e);
            throw new QuizServiceException("Failed to fetch attempts", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttemptResponse> getQuizAttempts(UUID quizId) {
        try {
            return quizAttemptRepository.findByQuizIdOrderByCreatedAtDesc(quizId)
                    .stream().map(attemptMapper::toResponse).toList();
        } catch (Exception e) {
            log.error("Failed to fetch attempts for quiz {}: {}", quizId, e.getMessage(), e);
            throw new QuizServiceException("Failed to fetch attempts", e);
        }
    }

    private String serializeAnswers(Map<UUID, String> answers) {
        try {
            return objectMapper.writeValueAsString(answers);
        } catch (Exception e) {
            log.warn("Failed to serialize answers: {}", e.getMessage());
            return "{}";
        }
    }

    @SuppressWarnings("unused")
    private Map<UUID, String> parseAnswers(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<UUID, String>>() {
            });
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    @SuppressWarnings("unused")
    private void validateOwnership(QuizAttempt attempt, UUID userId) {
        if (!attempt.getUserId().equals(userId)) {
            throw new AccessDeniedException("Attempt does not belong to user");
        }
    }
}