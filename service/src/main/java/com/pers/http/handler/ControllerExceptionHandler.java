package com.pers.http.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice(basePackages = "com.pers.http.controller")
public class ControllerExceptionHandler {
// todo переделать на рест
//    @ExceptionHandler(Exception.class)
//    public String handleException(Exception exception) {
//        log.error("Failed to return response", exception);
//        return "error/error500";
//    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public String handleException(AccessDeniedException exception) {
//        log.error("Failed to return response", exception);
//        return "error/error403";
//    }
}
