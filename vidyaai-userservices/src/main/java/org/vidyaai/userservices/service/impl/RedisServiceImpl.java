package org.vidyaai.userservices.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.vidyaai.userservices.service.RedisService;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void save(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("Saved to Redis with key: {}", key);
        } catch (Exception e) {
            log.error("Error saving to Redis with key: {}", key, e);
            throw new RuntimeException("Failed to save to Redis", e);
        }
    }

    @Override
    public void save(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.debug("Saved to Redis with key: {}, timeout: {} {}", key, timeout, unit);
        } catch (Exception e) {
            log.error("Error saving to Redis with key: {}", key, e);
            throw new RuntimeException("Failed to save to Redis", e);
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.debug("No value found in Redis for key: {}", key);
                return null;
            }
            return objectMapper.convertValue(value, clazz);
        } catch (IllegalArgumentException e) {
            log.error("Error converting Redis value for key: {}", key, e);
            return null;
        } catch (Exception e) {
            log.error("Error getting from Redis with key: {}", key, e);
            return null;
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Error checking Redis key existence: {}", key, e);
            return false;
        }
    }

    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Deleted Redis key: {}", key);
        } catch (Exception e) {
            log.error("Error deleting Redis key: {}", key, e);
        }
    }

    @Override
    public void deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Deleted {} Redis keys matching pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            log.error("Error deleting Redis keys by pattern: {}", pattern, e);
        }
    }

    @Override
    public Long increment(String key) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            log.error("Error incrementing Redis key: {}", key, e);
            return null;
        }
    }

    @Override
    public void expire(String key, long timeout, TimeUnit unit) {
        try {
            redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            log.error("Error setting expiry for Redis key: {}", key, e);
        }
    }

    @Override
    public boolean isRateLimitExceeded(String key, int maxRequests, long windowInSeconds) {
        try {
            Long count = increment(key);
            if (count == null) {
                log.warn("Failed to increment rate limit counter for key: {}", key);
                return false;
            }
            if (count == 1) {
                expire(key, windowInSeconds, TimeUnit.SECONDS);
            }
            boolean exceeded = count > maxRequests;
            if (exceeded) {
                log.warn("Rate limit exceeded for key: {}, count: {}, max: {}", key, count, maxRequests);
            }
            return exceeded;
        } catch (Exception e) {
            log.error("Error checking rate limit for key: {}", key, e);
            return false;
        }
    }
}