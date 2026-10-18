package org.vidyaai.videoservices.producer.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.vidyaai.videoservices.constant.KafkaTopics;
import org.vidyaai.videoservices.entity.Video;
import org.vidyaai.videoservices.event.VideoDeletedEvent;
import org.vidyaai.videoservices.event.VideoUpdatedEvent;
import org.vidyaai.videoservices.event.VideoUploadedEvent;
import org.vidyaai.videoservices.producer.VideoEventProducer;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class VideoEventProducerImpl implements VideoEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @CircuitBreaker(name = "kafkaProducer", fallbackMethod = "uploadedFallback")
    public void publishVideoUploaded(Video video) {
        try {
            VideoUploadedEvent event = VideoUploadedEvent.builder()
                    .eventId(UUID.randomUUID())
                    .videoId(video.getId())
                    .userId(video.getUserId())
                    .sourceType(video.getSourceType().name())
                    .sourceUrl(video.getSourceUrl())
                    .filePath(video.getFilePath())
                    .title(video.getTitle())
                    .durationSeconds(video.getDurationSeconds())
                    .uploadedAt(LocalDateTime.now())
                    .build();

            kafkaTemplate.send(KafkaTopics.VIDEO_UPLOADED,
                    video.getId().toString(), event);
            log.info("Published VIDEO_UPLOADED videoId={}", video.getId());
        } catch (Exception e) {
            log.error("Failed publishing VIDEO_UPLOADED: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @CircuitBreaker(name = "kafkaProducer", fallbackMethod = "updatedFallback")
    public void publishVideoUpdated(Video video, String updatedField) {
        try {
            VideoUpdatedEvent event = VideoUpdatedEvent.builder()
                    .eventId(UUID.randomUUID())
                    .videoId(video.getId())
                    .userId(video.getUserId())
                    .updatedField(updatedField)
                    .updatedAt(LocalDateTime.now())
                    .build();
            kafkaTemplate.send(KafkaTopics.VIDEO_UPDATED, video.getId().toString(), event);
            log.info("Published VIDEO_UPDATED videoId={}", video.getId());
        } catch (Exception e) {
            log.error("Failed publishing VIDEO_UPDATED: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @CircuitBreaker(name = "kafkaProducer", fallbackMethod = "deletedFallback")
    public void publishVideoDeleted(Video video) {
        try {
            VideoDeletedEvent event = VideoDeletedEvent.builder()
                    .eventId(UUID.randomUUID())
                    .videoId(video.getId())
                    .userId(video.getUserId())
                    .deletedAt(LocalDateTime.now())
                    .build();
            kafkaTemplate.send(KafkaTopics.VIDEO_DELETED, video.getId().toString(), event);
            log.info("Published VIDEO_DELETED videoId={}", video.getId());
        } catch (Exception e) {
            log.error("Failed publishing VIDEO_DELETED: {}", e.getMessage(), e);
            throw e;
        }
    }

    // Fallbacks

    public void uploadedFallback(Video video, Throwable t) {
        log.error("Fallback: VIDEO_UPLOADED not published for {} - {}",
                video.getId(), t.getMessage());
    }

    public void updatedFallback(Video video, String field, Throwable t) {
        log.error("Fallback: VIDEO_UPDATED not published for {} - {}",
                video.getId(), t.getMessage());
    }

    public void deletedFallback(Video video, Throwable t) {
        log.error("Fallback: VIDEO_DELETED not published for {} - {}",
                video.getId(), t.getMessage());
    }
}