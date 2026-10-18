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
import org.vidyaai.aiservices.event.AiRegenerateEvent;
import org.vidyaai.aiservices.service.AiOrchestrationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiRegenerateListener {

    private final AiOrchestrationService aiOrchestrationService;

    @KafkaListener(
            topics = KafkaTopics.AI_REGENERATE,
            groupId = KafkaTopics.AI_SERVICE_GROUP,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onRegenerate(@Payload AiRegenerateEvent event,
                             @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                             @Header(KafkaHeaders.OFFSET) long offset,
                             Acknowledgment ack) {
        log.info("Received AiRegenerateEvent: videoId={}, contentType={}, partition={}, offset={}",
                event.getVideoId(), event.getContentType(), partition, offset);
        try {
            aiOrchestrationService.processRegenerateEvent(event);
            ack.acknowledge();
            log.info("AiRegenerateEvent processed successfully: videoId={}", event.getVideoId());
        } catch (Exception e) {
            log.error("Failed processing AiRegenerateEvent videoId={}: {}",
                    event.getVideoId(), e.getMessage(), e);
        }
    }
}