package org.vidyaai.videoservices.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.vidyaai.videoservices.exception.CacheOperationException;
import org.vidyaai.videoservices.service.CacheService;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @CircuitBreaker(name = "redisCache", fallbackMethod = "getFallback")
    public <T> Optional<T> get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.convertValue(value, clazz));
        } catch (IllegalArgumentException e) {
            log.error("Cache conversion failed for {}: {}", key, e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Cache get error for {}: {}", key, e.getMessage(), e);
            throw new CacheOperationException("Failed to read cache", e);
        }
    }

    @Override
    @CircuitBreaker(name = "redisCache", fallbackMethod = "putFallback")
    public void put(String key, Object value, Duration ttl) {
        try {
            if (value == null) {
                return;
            }
            if (ttl != null && !ttl.isZero() && !ttl.isNegative()) {
                redisTemplate.opsForValue().set(key, value, ttl);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
        } catch (Exception e) {
            log.error("Cache put error for {}: {}", key, e.getMessage(), e);
            throw new CacheOperationException("Failed to write cache", e);
        }
    }

    @Override
    @CircuitBreaker(name = "redisCache", fallbackMethod = "deleteFallback")
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("Cache delete error for {}: {}", key, e.getMessage(), e);
            throw new CacheOperationException("Failed to delete cache", e);
        }
    }

    @Override
    @CircuitBreaker(name = "redisCache", fallbackMethod = "deletePatternFallback")
    public void deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.error("Cache delete pattern error for {}: {}", pattern, e.getMessage(), e);
            throw new CacheOperationException("Failed to delete pattern", e);
        }
    }

    @Override
    @CircuitBreaker(name = "redisCache", fallbackMethod = "existsFallback")
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Cache exists error for {}: {}", key, e.getMessage(), e);
            throw new CacheOperationException("Failed to check cache", e);
        }
    }

    public <T> Optional<T> getFallback(String key, Class<T> clazz, Throwable t) {
        log.warn("Fallback cache get {}: {}", key, t.getMessage());
        return Optional.empty();
    }

    public void putFallback(String key, Object value, Duration ttl, Throwable t) {
        log.warn("Fallback cache put {}: {}", key, t.getMessage());
    }

    public boolean deleteFallback(String key, Throwable t) {
        log.warn("Fallback cache delete {}: {}", key, t.getMessage());
        return false;
    }

    public void deletePatternFallback(String pattern, Throwable t) {
        log.warn("Fallback cache delete pattern {}: {}", pattern, t.getMessage());
    }

    public boolean existsFallback(String key, Throwable t) {
        log.warn("Fallback cache exists {}: {}", key, t.getMessage());
        return false;
    }
}