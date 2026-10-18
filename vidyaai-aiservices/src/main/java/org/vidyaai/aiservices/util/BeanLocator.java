package org.vidyaai.aiservices.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BeanLocator implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext)
            throws BeansException {
        BeanLocator.context = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (context == null) {
            throw new IllegalStateException("ApplicationContext is not yet initialized");
        }
        try {
            return context.getBean(clazz);
        } catch (Exception e) {
            log.error("Failed to fetch bean of type {}: {}", clazz.getSimpleName(), e.getMessage());
            throw e;
        }
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        if (context == null) {
            throw new IllegalStateException("ApplicationContext is not yet initialized");
        }
        return context.getBean(name, clazz);
    }
}