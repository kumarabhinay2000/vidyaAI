package org.vidyaai.userservices.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.vidyaai.userservices.event.UserLoginEvent;
import org.vidyaai.userservices.event.UserRegisteredEvent;
import org.vidyaai.userservices.event.UserUpdatedEvent;
import org.vidyaai.userservices.model.constants.AppConstants;
import org.vidyaai.userservices.service.KafkaProducerService;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @CircuitBreaker(name = "kafkaProducer", fallbackMethod = "fallbackPublishUserRegisteredEvent")
    @Retry(name = "kafkaProducerRetry")
    public void publishUserRegisteredEvent(UserRegisteredEvent event) {
        try {
            log.info("Publishing UserRegisteredEvent for user: {}", event.getUserId());
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(AppConstants.TOPIC_USER_REGISTERED, event.getUserId().toString(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("UserRegisteredEvent published successfully for user: {}, offset: {}",
                            event.getUserId(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish UserRegisteredEvent for user: {}", event.getUserId(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error publishing UserRegisteredEvent for user: {}", event.getUserId(), e);
            throw e;
        }
    }

    @Override
    @CircuitBreaker(name = "kafkaProducer", fallbackMethod = "fallbackPublishUserLoginEvent")
    @Retry(name = "kafkaProducerRetry")
    public void publishUserLoginEvent(UserLoginEvent event) {
        try {
            log.info("Publishing UserLoginEvent for user: {}", event.getUserId());
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(AppConstants.TOPIC_USER_LOGIN, event.getUserId().toString(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("UserLoginEvent published successfully for user: {}, offset: {}",
                            event.getUserId(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish UserLoginEvent for user: {}", event.getUserId(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error publishing UserLoginEvent for user: {}", event.getUserId(), e);
            throw e;
        }
    }

    @Override
    @CircuitBreaker(name = "kafkaProducer", fallbackMethod = "fallbackPublishUserUpdatedEvent")
    @Retry(name = "kafkaProducerRetry")
    public void publishUserUpdatedEvent(UserUpdatedEvent event) {
        try {
            log.info("Publishing UserUpdatedEvent for user: {}", event.getUserId());
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(AppConstants.TOPIC_USER_UPDATED, event.getUserId().toString(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("UserUpdatedEvent published successfully for user: {}, offset: {}",
                            event.getUserId(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish UserUpdatedEvent for user: {}", event.getUserId(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error publishing UserUpdatedEvent for user: {}", event.getUserId(), e);
            throw e;
        }
    }

    // Fallback methods
    public void fallbackPublishUserRegisteredEvent(UserRegisteredEvent event, Throwable t) {
        log.error("Fallback: Failed to publish UserRegisteredEvent for user: {}. Error: {}",
                event.getUserId(), t.getMessage());
    }

    public void fallbackPublishUserLoginEvent(UserLoginEvent event, Throwable t) {
        log.error("Fallback: Failed to publish UserLoginEvent for user: {}. Error: {}",
                event.getUserId(), t.getMessage());
    }

    public void fallbackPublishUserUpdatedEvent(UserUpdatedEvent event, Throwable t) {
        log.error("Fallback: Failed to publish UserUpdatedEvent for user: {}. Error: {}",
                event.getUserId(), t.getMessage());
    }
}