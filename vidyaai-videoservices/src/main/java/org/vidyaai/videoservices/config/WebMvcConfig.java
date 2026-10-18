package org.vidyaai.videoservices.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    public WebMvcConfig() {
        log.info("WebMvcConfig initialized for Video Service");
    }

    @SuppressWarnings("unused")
    private void placeholder(@NonNull Object ignored) {
        // reserved for future interceptors (correlation id, rate limiting)
    }
}