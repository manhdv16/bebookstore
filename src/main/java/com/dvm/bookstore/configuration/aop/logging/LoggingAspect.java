package com.dvm.bookstore.configuration.aop.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Pointcut(
        "within(com.dvm.bookstore.repository..*) || within(com.dvm.bookstore.service..*) || within(com.dvm.bookstore.web.rest..*)"
    )
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Pointcut(
        "within(com.dvm.bookstore.controller..*)))"
    )
    public void controllerPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Around("controllerPackagePointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        log.info("Method {} executed in {} ms", joinPoint.getSignature(), executionTime);
        return proceed;
    }

    @Before("controllerPackagePointcut()")
    public void logMethodCall(ProceedingJoinPoint joinPoint) {
        log.info("Calling method: {}", joinPoint.getSignature());
    }

    @After("controllerPackagePointcut()")
    public void logMethodExit(ProceedingJoinPoint joinPoint) {
        log.info("Exiting method: {}", joinPoint.getSignature());
    }
    @AfterThrowing(pointcut = "controllerPackagePointcut()", throwing = "error")
    public void logMethodError(ProceedingJoinPoint joinPoint, Throwable error) {
        log.error("Error in method: {} with message: {}", joinPoint.getSignature(), error.getMessage());
    }
}
