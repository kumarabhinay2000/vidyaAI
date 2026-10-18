package org.vidyaai.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    public WebConfig() {
        log.info("WebConfig initialized for API Gateway");
    }

    @SuppressWarnings("unused")
    private void placeholder(@NonNull Object ignored) {
        // Reserved for future interceptors
    }
}