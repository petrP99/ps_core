package com.pers.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ServiceAspect {

//    @Pointcut("within(com.pers.service.*Service)")
//    public void isServiceLayer() {
//    }
//
//    @Before("isServiceLayer()")
//    public void addLogging(JoinPoint joinPoint) {
//        var methodName = joinPoint.getSignature().getName();
//        var className = joinPoint.getTarget().getClass().getName();
//        var args = joinPoint.getArgs();
//        log.info("Before: invoked method: {} in class: {} with args: {}", methodName, className, args);
//    }
//
//    @AfterReturning(value = "isServiceLayer()", returning = "result")
//    public void addLoggingReturnValues(JoinPoint joinPoint, Object result) {
//        String methodName = joinPoint.getSignature().getName();
//        String className = joinPoint.getSignature().getDeclaringTypeName();
//        log.info("After Returning: invoked method: {} in class: {} with returning value: {}", methodName, className, result);
//    }
}
