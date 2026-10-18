package org.vidyaai.userservices.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {

    void save(String key, Object value);
    void save(String key, Object value, long timeout, TimeUnit unit);
    <T> T get(String key, Class<T> clazz);
    boolean exists(String key);
    void delete(String key);
    void deleteByPattern(String pattern);
    Long increment(String key);
    void expire(String key, long timeout, TimeUnit unit);
    boolean isRateLimitExceeded(String key, int maxRequests, long windowInSeconds);
}