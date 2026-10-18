package org.vidyaai.aiservices.producer.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.constant.KafkaTopics;
import org.vidyaai.aiservices.event.AiProcessingCompletedEvent;
import org.vidyaai.aiservices.event.AiProcessingFailedEvent;
import org.vidyaai.aiservices.producer.AiEventProducer;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiEventProducerImpl implements AiEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @CircuitBreaker(name = "kafkaProducer", fallbackMethod = "completedFallback")
    public void publishProcessingCompleted(AiProcessingCompletedEvent event) {
        try {
            String key = event.getVideoId() != null ? event.getVideoId().toString() : "unknown";
            log.info("Publishing AI_PROCESSING_COMPLETED for videoId={}", key);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                    KafkaTopics.AI_PROCESSING_COMPLETED, key, event);

            future.whenComplete((result, ex) -> {
                if (ex == null && result != null) {
                    log.info("AI_PROCESSING_COMPLETED published: offset={}",
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish AI_PROCESSING_COMPLETED: {}",
                            ex != null ? ex.getMessage() : "unknown");
                }
            });
        } catch (Exception e) {
            log.error("Error publishing AI_PROCESSING_COMPLETED event", e);
            throw e;
        }
    }

    @Override
    @CircuitBreaker(name = "kafkaProducer", fallbackMethod = "failedFallback")
    public void publishProcessingFailed(AiProcessingFailedEvent event) {
        try {
            String key = event.getVideoId() != null ? event.getVideoId().toString() : "unknown";
            log.warn("Publishing AI_PROCESSING_FAILED for videoId={}, reason={}",
                    key, event.getErrorMessage());

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                    KafkaTopics.AI_PROCESSING_FAILED, key, event);

            future.whenComplete((result, ex) -> {
                if (ex == null && result != null) {
                    log.info("AI_PROCESSING_FAILED published: offset={}",
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish AI_PROCESSING_FAILED: {}",
                            ex != null ? ex.getMessage() : "unknown");
                }
            });
        } catch (Exception e) {
            log.error("Error publishing AI_PROCESSING_FAILED event", e);
            throw e;
        }
    }

    public void completedFallback(AiProcessingCompletedEvent event, Throwable t) {
        log.error("Fallback: AI_PROCESSING_COMPLETED could not be published for videoId={} - {}",
                event.getVideoId(), t.getMessage());
    }

    public void failedFallback(AiProcessingFailedEvent event, Throwable t) {
        log.error("Fallback: AI_PROCESSING_FAILED could not be published for videoId={} - {}",
                event.getVideoId(), t.getMessage());
    }
}