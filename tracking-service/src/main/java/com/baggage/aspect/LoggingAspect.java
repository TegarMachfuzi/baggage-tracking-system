package com.baggage.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.baggage.controller..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        logger.info("Controller method called: {}", methodName);
        
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        
        logger.info("Controller method {} completed in {}ms", methodName, duration);
        return result;
    }

    @Around("execution(* com.baggage.service..*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        logger.info("Service method called: {}", methodName);
        
        try {
            Object result = joinPoint.proceed();
            logger.info("Service method {} completed successfully", methodName);
            return result;
        } catch (Exception e) {
            logger.error("Service method {} failed: {}", methodName, e.getMessage());
            throw e;
        }
    }
}
