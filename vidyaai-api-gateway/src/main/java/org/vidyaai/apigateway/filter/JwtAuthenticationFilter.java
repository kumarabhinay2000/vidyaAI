package org.vidyaai.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.vidyaai.apigateway.config.GatewayProperties;
import org.vidyaai.apigateway.constant.AppConstants;
import org.vidyaai.apigateway.security.JwtValidator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;
    private final GatewayProperties gatewayProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Skip CORS preflight
        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Public paths skip
        if (isPublicPath(path)) {
            log.debug("Public path, skipping JWT validation: {} {}", method, path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);
        if (!StringUtils.hasText(token)) {
            log.warn("Missing Authorization header for {} {}", method, path);
            writeError(response, HttpStatus.UNAUTHORIZED, AppConstants.ERROR_UNAUTHORIZED,
                    "Authorization token required");
            return;
        }

        try {
            Claims claims = jwtValidator.validateAndExtract(token);
            String userId = claims.get(AppConstants.USER_ID_CLAIM, String.class);
            String email = claims.get(AppConstants.EMAIL_CLAIM, String.class);
            String role = claims.get(AppConstants.ROLE_CLAIM, String.class);
            String plan = claims.get(AppConstants.PLAN_CLAIM, String.class);

            // Propagate user context to downstream services
            UserContextRequestWrapper wrapped = new UserContextRequestWrapper(request);
            if (userId != null) wrapped.putHeader(AppConstants.USER_ID_HEADER, userId);
            if (email != null) wrapped.putHeader(AppConstants.USER_EMAIL_HEADER, email);
            if (role != null) wrapped.putHeader(AppConstants.USER_ROLE_HEADER, role);
            if (plan != null) wrapped.putHeader(AppConstants.USER_PLAN_HEADER, plan);

            log.debug("JWT validated for userId={} role={} path={}", userId, role, path);
            filterChain.doFilter(wrapped, response);

        } catch (ExpiredJwtException e) {
            log.warn("JWT expired for {} {}", method, path);
            writeError(response, HttpStatus.UNAUTHORIZED, AppConstants.ERROR_TOKEN_EXPIRED,
                    "Token has expired");
        } catch (Exception e) {
            log.warn("JWT invalid for {} {}: {}", method, path, e.getMessage());
            writeError(response, HttpStatus.UNAUTHORIZED, AppConstants.ERROR_INVALID_TOKEN,
                    "Invalid token");
        }
    }

    private boolean isPublicPath(String path) {
        List<String> publicPaths = gatewayProperties.getSecurity().getPublicPaths();
        if (publicPaths == null || publicPaths.isEmpty()) {
            return false;
        }
        return publicPaths.stream().anyMatch(p -> pathMatcher.match(p, path));
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader(AppConstants.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearer) && bearer.startsWith(AppConstants.BEARER_PREFIX)) {
            return bearer.substring(AppConstants.BEARER_PREFIX.length());
        }
        return null;
    }

    private void writeError(HttpServletResponse response, HttpStatus status,
                            String errorCode, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String body = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"errorCode\":\"%s\",\"message\":\"%s\"}",
                LocalDateTime.now(), status.value(), status.getReasonPhrase(), errorCode, message
        );
        response.getWriter().write(body);
    }
}