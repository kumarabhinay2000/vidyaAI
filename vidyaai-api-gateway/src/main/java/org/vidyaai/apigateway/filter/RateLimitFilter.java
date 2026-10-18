package org.vidyaai.apigateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.vidyaai.apigateway.config.GatewayProperties;
import org.vidyaai.apigateway.constant.AppConstants;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final GatewayProperties gatewayProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (!gatewayProperties.getRateLimit().isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String clientKey = resolveClientKey(request, path);
        int limit = resolveLimit(path);
        String redisKey = "ratelimit:" + clientKey + ":" + path;

        try {
            Long count = redisTemplate.opsForValue().increment(redisKey);
            if (count != null && count == 1L) {
                redisTemplate.expire(redisKey, 60, TimeUnit.SECONDS);
            }

            long remaining = Math.max(0, limit - (count != null ? count : 0));
            response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));

            if (count != null && count > limit) {
                log.warn("Rate limit exceeded for client={} path={} count={}",
                        clientKey, path, count);
                writeError(response);
                return;
            }
        } catch (Exception e) {
            log.warn("Rate limit check failed (fail-open): {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientKey(HttpServletRequest request, String path) {
        String userId = request.getHeader(AppConstants.USER_ID_HEADER);
        if (StringUtils.hasText(userId)) {
            return "user:" + userId;
        }
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            return "ip:" + xff.split(",")[0].trim();
        }
        return "ip:" + request.getRemoteAddr();
    }

    private int resolveLimit(String path) {
        if (path.startsWith("/api/auth/")) {
            return gatewayProperties.getRateLimit().getAuthRequestsPerMinute();
        }
        return gatewayProperties.getRateLimit().getDefaultRequestsPerMinute();
    }

    private void writeError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String body = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"TOO_MANY_REQUESTS\"," +
                        "\"errorCode\":\"%s\",\"message\":\"%s\"}",
                LocalDateTime.now(), HttpStatus.TOO_MANY_REQUESTS.value(),
                AppConstants.ERROR_RATE_LIMIT,
                "Too many requests. Please try again later."
        );
        response.getWriter().write(body);
    }
}