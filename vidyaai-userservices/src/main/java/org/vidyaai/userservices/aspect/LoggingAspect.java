package org.vidyaai.userservices.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(org.vidyaai.userservices.controller..*)")
    public void controllerMethods() {
    }

    @Pointcut("within(org.vidyaai.userservices.service..*)")
    public void serviceMethods() {
    }

    @Around("controllerMethods()")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info("Entering Controller: {} with args: {}", methodName, Arrays.toString(args));
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long timeTaken = System.currentTimeMillis() - startTime;
            log.info("Exiting Controller: {} with result: {}, time taken: {}ms",
                    methodName, result, timeTaken);
            return result;
        } catch (Throwable throwable) {
            long timeTaken = System.currentTimeMillis() - startTime;
            log.error("Exception in Controller: {} after {}ms - {}",
                    methodName, timeTaken, throwable.getMessage(), throwable);
            throw throwable;
        }
    }

    @Around("serviceMethods()")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();

        log.debug("Entering Service: {}", methodName);
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long timeTaken = System.currentTimeMillis() - startTime;
            log.debug("Exiting Service: {}, time taken: {}ms", methodName, timeTaken);
            return result;
        } catch (Throwable throwable) {
            long timeTaken = System.currentTimeMillis() - startTime;
            log.error("Exception in Service: {} after {}ms - {}",
                    methodName, timeTaken, throwable.getMessage(), throwable);
            throw throwable;
        }
    }
}