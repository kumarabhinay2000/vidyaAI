package org.vidyaai.aiservices.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.constant.KafkaTopics;
import org.vidyaai.aiservices.event.VideoUploadedEvent;
import org.vidyaai.aiservices.service.AiOrchestrationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class VideoUploadedListener {

    private final AiOrchestrationService aiOrchestrationService;

    @KafkaListener(
            topics = KafkaTopics.VIDEO_UPLOADED,
            groupId = KafkaTopics.AI_SERVICE_GROUP,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onVideoUploaded(@Payload VideoUploadedEvent event,
                                @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                @Header(KafkaHeaders.OFFSET) long offset,
                                Acknowledgment ack) {
        log.info("Received VideoUploadedEvent: videoId={}, partition={}, offset={}",
                event.getVideoId(), partition, offset);
        try {
            aiOrchestrationService.processVideoUploadedEvent(event);
            ack.acknowledge();
            log.info("VideoUploadedEvent processed successfully: videoId={}", event.getVideoId());
        } catch (Exception e) {
            log.error("Failed processing VideoUploadedEvent videoId={}: {}",
                    event.getVideoId(), e.getMessage(), e);
            // Manual ack — do NOT ack on failure so it can be retried (idempotency required upstream)
        }
    }
}