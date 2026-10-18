package org.vidyaai.userservices.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.vidyaai.userservices.exception.RateLimitExceededException;
import org.vidyaai.userservices.model.constants.AppConstants;
import org.vidyaai.userservices.service.RedisService;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RedisService redisService;

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {

        try {
            String clientIp = getClientIp(request);
            String uri = request.getRequestURI();

            int maxRequests = uri.startsWith("/api/auth/")
                    ? AppConstants.AUTH_RATE_LIMIT
                    : AppConstants.DEFAULT_RATE_LIMIT;

            String key = AppConstants.REDIS_RATE_LIMIT_PREFIX + clientIp + ":" + uri;

            if (redisService.isRateLimitExceeded(key, maxRequests, 60)) {
                log.warn("Rate limit exceeded for IP: {} on URI: {}", clientIp, uri);
                throw new RateLimitExceededException(
                        "Too many requests. Please try again later.",
                        AppConstants.ERROR_RATE_LIMIT_EXCEEDED
                );
            }

            return true;
        } catch (RateLimitExceededException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error in rate limit interceptor: {}", e.getMessage());
            return true;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        try {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            log.error("Error getting client IP: {}", e.getMessage());
            return "unknown";
        }
    }
}