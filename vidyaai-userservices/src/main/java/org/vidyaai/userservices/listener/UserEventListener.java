package org.vidyaai.userservices.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.vidyaai.userservices.event.UserLoginEvent;
import org.vidyaai.userservices.event.UserRegisteredEvent;
import org.vidyaai.userservices.event.UserUpdatedEvent;


@Component
@RequiredArgsConstructor
@Slf4j

public class UserEventListener {

    @Async
    @EventListener

    public void handleUserRegisteredEvent(UserRegisteredEvent event) {

        log.info("Received UserRegisteredEvent for user: {}", event.getUserId());
        try {

            log.info("Processing UserRegisteredEvent - User: {}, Email: {}, Role {}",
                    event.getUserId(), event.getEmail(), event.getRole());
        }catch(Exception e)
        {
            log.error("Error processing UserRegisteredEvent for user: {}", event.getUserId(), e);
        }
    }

    @Async
    @EventListener
    public void handleUserLoginEvent(UserLoginEvent event) {
        log.info("Received UserLoginEvent for User: {}", event.getUserId());

        try {
            log.info("Processing UserLoginEvent -User: {}, IP: {}, Time: {}",
                    event.getUserId(), event.getIpAddress(), event.getLoginAt());
        } catch(Exception e) {
            log.error("Error processing UserLoginEvent for user: {}",
                    event.getUserId(), e);
        }

    }
    @Async
    @EventListener
    public void handleUserUpdatedEvent(UserUpdatedEvent event) {
        log.info("Received UserUpdatedEvent for user: {}", event.getUserId());

        try {
            log.info("Processing UserUpdatedEvent - User: {}, Field: {}",
                    event.getUserId(), event.getUpdatedField());
        } catch(Exception e)
        {
            log.error("Error processing UserUpdatedEvent for user: {}",
                    event.getUserId(), e);
        }
    }
}