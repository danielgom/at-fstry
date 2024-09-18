package com.encora.synth.aitooling.controller;

import com.encora.synth.aitooling.dto.UserLoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    // Pointcut that matches all endpoints in the controller package
    @Pointcut("within(com.encora.synth.aitooling.controller..*)")
    public void controllerMethods() {
    }

    @Before("controllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        log.info("Incoming Request: [Method: {}] [URI: {}] [IP: {}]", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        log.info("Handling {} with arguments: {}", joinPoint.getSignature(), makeSensitiveData(joinPoint.getArgs()));
    }

    // After returning advice that logs details after the method execution
    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Completed {} with result: {}", joinPoint.getSignature(), result);
    }

    private String makeSensitiveData(Object[] args) {
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg instanceof UserLoginRequest loginRequest) {
                        return new UserLoginRequest(loginRequest.getEmail(), "****");
                    }
                    return arg;
                })
                .toList().toString();
    }
}
