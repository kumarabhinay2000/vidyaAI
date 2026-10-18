package org.vidyaai.quizservices.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.vidyaai.quizservices.constant.KafkaTopics;
import org.vidyaai.quizservices.event.AiProcessingCompletedEvent;
import org.vidyaai.quizservices.event.AiProcessingFailedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiProcessingListener {

    @KafkaListener(
            topics = KafkaTopics.AI_PROCESSING_COMPLETED,
            groupId = KafkaTopics.QUIZ_SERVICE_GROUP,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onAiCompleted(@Payload AiProcessingCompletedEvent event,
                              @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                              @Header(KafkaHeaders.OFFSET) long offset,
                              Acknowledgment ack) {
        log.info("Received AI_PROCESSING_COMPLETED: videoId={} quizId={} partition={} offset={}",
                event.getVideoId(), event.getQuizId(), partition, offset);
        try {
            // AI Service creates the quiz/analytics on its side; here we just log
            // until a shared persistence / sync mechanism is wired in.
            log.info("AI quiz ready for videoId={} quizId={}",
                    event.getVideoId(), event.getQuizId());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed handling AI_PROCESSING_COMPLETED for videoId={}: {}",
                    event.getVideoId(), e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = KafkaTopics.AI_PROCESSING_FAILED,
            groupId = KafkaTopics.QUIZ_SERVICE_GROUP,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onAiFailed(@Payload AiProcessingFailedEvent event,
                           @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                           @Header(KafkaHeaders.OFFSET) long offset,
                           Acknowledgment ack) {
        log.warn("Received AI_PROCESSING_FAILED: videoId={} step={} reason={} partition={} offset={}",
                event.getVideoId(), event.getStep(), event.getErrorMessage(), partition, offset);
        try {
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed handling AI_PROCESSING_FAILED: {}", e.getMessage(), e);
        }
    }
}