package org.vidyaai.quizservices.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.vidyaai.quizservices.constant.KafkaTopics;
import org.vidyaai.quizservices.entity.QuizAttempt;
import org.vidyaai.quizservices.event.QuizAttemptedEvent;
import org.vidyaai.quizservices.event.QuizCompletedEvent;
import org.vidyaai.quizservices.service.QuizEventProducer;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizEventProducerImpl implements QuizEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @CircuitBreaker(name = "kafkaProducer", fallbackMethod = "attemptedFallback")
    public void publishQuizAttempted(QuizAttempt attempt, UUID videoId) {
        try {
            QuizAttemptedEvent event = QuizAttemptedEvent.builder()
                    .eventId(UUID.randomUUID())
                    .attemptId(attempt.getId())
                    .quizId(attempt.getQuizId())
                    .userId(attempt.getUserId())
                    .videoId(videoId)
                    .score(attempt.getScore())
                    .totalMarks(attempt.getTotalMarks())
                    .percentage(attempt.getPercentage())
                    .isPassed(attempt.getIsPassed())
                    .attemptedAt(LocalDateTime.now())
                    .build();

            kafkaTemplate.send(KafkaTopics.QUIZ_ATTEMPTED,
                    attempt.getUserId().toString(), event);
            log.info("Published QUIZ_ATTEMPTED attemptId={}", attempt.getId());
        } catch (Exception e) {
            log.error("Failed publishing QUIZ_ATTEMPTED: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @CircuitBreaker(name = "kafkaProducer", fallbackMethod = "completedFallback")
    public void publishQuizCompleted(QuizAttempt attempt, UUID videoId) {
        try {
            QuizCompletedEvent event = QuizCompletedEvent.builder()
                    .eventId(UUID.randomUUID())
                    .attemptId(attempt.getId())
                    .quizId(attempt.getQuizId())
                    .userId(attempt.getUserId())
                    .videoId(videoId)
                    .finalScore(attempt.getScore())
                    .percentage(attempt.getPercentage())
                    .passed(attempt.getIsPassed())
                    .timeTakenSeconds(attempt.getTimeTakenSeconds())
                    .completedAt(LocalDateTime.now())
                    .build();

            kafkaTemplate.send(KafkaTopics.QUIZ_COMPLETED,
                    attempt.getUserId().toString(), event);
            log.info("Published QUIZ_COMPLETED attemptId={}", attempt.getId());
        } catch (Exception e) {
            log.error("Failed publishing QUIZ_COMPLETED: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void attemptedFallback(QuizAttempt attempt, UUID videoId, Throwable t) {
        log.error("Fallback: QUIZ_ATTEMPTED not published for attemptId={} - {}",
                attempt.getId(), t.getMessage());
    }

    public void completedFallback(QuizAttempt attempt, UUID videoId, Throwable t) {
        log.error("Fallback: QUIZ_COMPLETED not published for attemptId={} - {}",
                attempt.getId(), t.getMessage());
    }
}