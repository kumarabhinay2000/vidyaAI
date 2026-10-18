package org.vidyaai.aiservices.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.event.AiProcessingCompletedEvent;
import org.vidyaai.aiservices.event.AiProcessingFailedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiProcessingEventListener {

    @Async("aiTaskExecutor")
    @EventListener
    public void onCompleted(AiProcessingCompletedEvent event) {
        log.info("Internal event: AI processing COMPLETED for videoId={}, contentTypes={}, cached={}",
                event.getVideoId(), event.getContentTypes(), event.getCacheHit());
    }

    @Async("aiTaskExecutor")
    @EventListener
    public void onFailed(AiProcessingFailedEvent event) {
        log.warn("Internal event: AI processing FAILED for videoId={}, step={}, reason={}",
                event.getVideoId(), event.getStep(), event.getErrorMessage());
    }
}