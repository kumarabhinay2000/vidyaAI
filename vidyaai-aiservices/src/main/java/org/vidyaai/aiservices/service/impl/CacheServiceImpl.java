package org.vidyaai.aiservices.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.vidyaai.aiservices.exception.CacheOperationException;
import org.vidyaai.aiservices.service.CacheService;

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
                log.debug("Cache MISS: {}", key);
                return Optional.empty();
            }
            log.debug("Cache HIT: {}", key);
            return Optional.of(objectMapper.convertValue(value, clazz));
        } catch (IllegalArgumentException e) {
            log.error("Cache conversion failed for key {}: {}", key, e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Cache get error for key {}: {}", key, e.getMessage(), e);
            throw new CacheOperationException("Failed to read from cache", e);
        }
    }

    @Override
    @CircuitBreaker(name = "redisCache", fallbackMethod = "putFallback")
    public void put(String key, Object value, Duration ttl) {
        try {
            if (value == null) {
                log.debug("Skipping cache put for null value: {}", key);
                return;
            }
            if (ttl != null && !ttl.isZero() && !ttl.isNegative()) {
                redisTemplate.opsForValue().set(key, value, ttl);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            log.debug("Cache PUT: {} (ttl={})", key, ttl);
        } catch (Exception e) {
            log.error("Cache put error for key {}: {}", key, e.getMessage(), e);
            throw new CacheOperationException("Failed to write to cache", e);
        }
    }

    @Override
    @CircuitBreaker(name = "redisCache", fallbackMethod = "deleteFallback")
    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            log.debug("Cache DELETE: {} (result={})", key, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Cache delete error for key {}: {}", key, e.getMessage(), e);
            throw new CacheOperationException("Failed to delete from cache", e);
        }
    }

    @Override
    @CircuitBreaker(name = "redisCache", fallbackMethod = "deletePatternFallback")
    public void deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Cache DELETE pattern: {} ({} keys)", pattern, keys.size());
            }
        } catch (Exception e) {
            log.error("Cache delete pattern error for {}: {}", pattern, e.getMessage(), e);
            throw new CacheOperationException("Failed to delete by pattern", e);
        }
    }

    @Override
    @CircuitBreaker(name = "redisCache", fallbackMethod = "existsFallback")
    public boolean exists(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Cache exists error for key {}: {}", key, e.getMessage(), e);
            throw new CacheOperationException("Failed to check cache existence", e);
        }
    }

    @Override
    @CircuitBreaker(name = "redisCache", fallbackMethod = "incrementFallback")
    public Long increment(String key) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            log.error("Cache increment error for key {}: {}", key, e.getMessage(), e);
            throw new CacheOperationException("Failed to increment cache key", e);
        }
    }

    @Override
    @CircuitBreaker(name = "redisCache", fallbackMethod = "expireFallback")
    public void expire(String key, Duration ttl) {
        try {
            if (ttl != null && !ttl.isZero() && !ttl.isNegative()) {
                redisTemplate.expire(key, ttl);
                log.debug("Cache EXPIRE: {} (ttl={})", key, ttl);
            }
        } catch (Exception e) {
            log.error("Cache expire error for key {}: {}", key, e.getMessage(), e);
            throw new CacheOperationException("Failed to set cache expiry", e);
        }
    }

    // ---------- fallbacks ----------

    public <T> Optional<T> getFallback(String key, Class<T> clazz, Throwable t) {
        log.warn("Fallback: Cache get failed for key {}: {}", key, t.getMessage());
        return Optional.empty();
    }

    public void putFallback(String key, Object value, Duration ttl, Throwable t) {
        log.warn("Fallback: Cache put skipped for key {}: {}", key, t.getMessage());
    }

    public boolean deleteFallback(String key, Throwable t) {
        log.warn("Fallback: Cache delete skipped for key {}: {}", key, t.getMessage());
        return false;
    }

    public void deletePatternFallback(String pattern, Throwable t) {
        log.warn("Fallback: Cache delete pattern skipped for {}: {}", pattern, t.getMessage());
    }

    public boolean existsFallback(String key, Throwable t) {
        log.warn("Fallback: Cache exists check failed for {}: {}", key, t.getMessage());
        return false;
    }

    public Long incrementFallback(String key, Throwable t) {
        log.warn("Fallback: Cache increment failed for {}: {}", key, t.getMessage());
        return 0L;
    }

    public void expireFallback(String key, Duration ttl, Throwable t) {
        log.warn("Fallback: Cache expire skipped for {}: {}", key, t.getMessage());
    }
}