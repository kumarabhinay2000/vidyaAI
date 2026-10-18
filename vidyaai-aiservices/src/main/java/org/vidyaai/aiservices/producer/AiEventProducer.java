package org.vidyaai.aiservices.producer;

import org.vidyaai.aiservices.event.AiProcessingCompletedEvent;
import org.vidyaai.aiservices.event.AiProcessingFailedEvent;

public interface AiEventProducer {

    void publishProcessingCompleted(AiProcessingCompletedEvent event);

    void publishProcessingFailed(AiProcessingFailedEvent event);
}