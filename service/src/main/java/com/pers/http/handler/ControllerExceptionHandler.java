package com.pers.http.handler;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(basePackages = "com.pers.http.rest")
public class ControllerExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatusException(
            ResponseStatusException exception
    ) {
        String detail = exception.getReason() != null
                ? exception.getReason()
                : exception.getStatusCode().toString();
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                exception.getStatusCode(),
                detail
        );

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(problem);
    }
}
