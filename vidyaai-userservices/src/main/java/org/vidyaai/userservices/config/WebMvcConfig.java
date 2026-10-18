package org.vidyaai.userservices.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.vidyaai.userservices.interceptor.RateLimitInterceptor;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        log.info("Registering RateLimitInterceptor");
        try {
            registry.addInterceptor(rateLimitInterceptor)
                    .addPathPatterns("/api/**")
                    .excludePathPatterns(
                            "/actuator/**",
                            "/error",
                            "/swagger-ui/**",
                            "/v3/api-docs/**"
                    );
        } catch (Exception e) {
            log.error("Error registering interceptors: {}", e.getMessage(), e);
            throw e;
        }
    }
}