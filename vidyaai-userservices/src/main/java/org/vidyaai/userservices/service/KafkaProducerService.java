package org.vidyaai.userservices.service;

import org.vidyaai.userservices.event.UserLoginEvent;
import org.vidyaai.userservices.event.UserRegisteredEvent;
import org.vidyaai.userservices.event.UserUpdatedEvent;

public interface KafkaProducerService {

    void publishUserRegisteredEvent(UserRegisteredEvent event);
    void publishUserLoginEvent(UserLoginEvent event);
    void publishUserUpdatedEvent(UserUpdatedEvent event);

}