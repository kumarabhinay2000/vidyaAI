package org.vidyaai.apigateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.vidyaai.apigateway.config.GatewayProperties;
import org.vidyaai.apigateway.constant.AppConstants;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(0)
@RequiredArgsConstructor
@Slf4j
public class CorrelationIdFilter extends OncePerRequestFilter {

    private final GatewayProperties gatewayProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId = request.getHeader(AppConstants.CORRELATION_ID_HEADER);
        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);

        try {
            UserContextRequestWrapper wrapped = new UserContextRequestWrapper(request);
            wrapped.putHeader(AppConstants.CORRELATION_ID_HEADER, correlationId);

            response.setHeader(AppConstants.CORRELATION_ID_HEADER, correlationId);

            log.debug("Request {} {} correlationId={}",
                    request.getMethod(), request.getRequestURI(), correlationId);

            filterChain.doFilter(wrapped, response);
        } finally {
            MDC.remove("correlationId");
        }
    }
}