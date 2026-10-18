package org.vidyaai.aiservices.service;

import org.vidyaai.aiservices.event.AiRegenerateEvent;
import org.vidyaai.aiservices.event.VideoUploadedEvent;

public interface AiOrchestrationService {

    void processVideoUploadedEvent(VideoUploadedEvent event);

    void processRegenerateEvent(AiRegenerateEvent event);
}