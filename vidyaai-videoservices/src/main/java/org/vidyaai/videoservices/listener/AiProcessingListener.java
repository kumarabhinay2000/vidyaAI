package org.vidyaai.videoservices.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.vidyaai.videoservices.constant.KafkaTopics;
import org.vidyaai.videoservices.entity.Video;
import org.vidyaai.videoservices.enums.VideoStatus;
import org.vidyaai.videoservices.event.AiProcessingCompletedEvent;
import org.vidyaai.videoservices.event.AiProcessingFailedEvent;
import org.vidyaai.videoservices.repository.VideoRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiProcessingListener {

    private final VideoRepository videoRepository;

    @KafkaListener(
            topics = KafkaTopics.AI_PROCESSING_COMPLETED,
            groupId = KafkaTopics.VIDEO_SERVICE_GROUP,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onAiCompleted(@Payload AiProcessingCompletedEvent event,
                              @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                              @Header(KafkaHeaders.OFFSET) long offset,
                              Acknowledgment ack) {
        log.info("Received AI_PROCESSING_COMPLETED videoId={} contentTypes={} partition={} offset={}",
                event.getVideoId(), event.getContentTypes(), partition, offset);
        try {
            Optional<Video> videoOpt = videoRepository.findByIdAndIsDeletedFalse(event.getVideoId());
            if (videoOpt.isPresent()) {
                Video video = videoOpt.get();
                video.markCompleted();
                videoRepository.save(video);
                log.info("Video marked COMPLETED: {}", video.getId());
            } else {
                log.warn("Video not found for AI completion event: {}", event.getVideoId());
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed handling AI_PROCESSING_COMPLETED for {}: {}",
                    event.getVideoId(), e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = KafkaTopics.AI_PROCESSING_FAILED,
            groupId = KafkaTopics.VIDEO_SERVICE_GROUP,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onAiFailed(@Payload AiProcessingFailedEvent event,
                           @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                           @Header(KafkaHeaders.OFFSET) long offset,
                           Acknowledgment ack) {
        log.warn("Received AI_PROCESSING_FAILED videoId={} step={} reason={}",
                event.getVideoId(), event.getStep(), event.getErrorMessage());
        try {
            videoRepository.findByIdAndIsDeletedFalse(event.getVideoId()).ifPresent(video -> {
                video.markFailed();
                videoRepository.save(video);
                log.warn("Video marked FAILED: {}", video.getId());
            });
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed handling AI_PROCESSING_FAILED: {}", e.getMessage(), e);
        }
    }
}