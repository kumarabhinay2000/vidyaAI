package org.vidyaai.aiservices.service;

import java.time.Duration;
import java.util.Optional;

public interface CacheService {

    <T> Optional<T> get(String key, Class<T> clazz);

    void put(String key, Object value, Duration ttl);

    boolean delete(String key);

    void deleteByPattern(String pattern);

    boolean exists(String key);

    Long increment(String key);

    void expire(String key, Duration ttl);
}