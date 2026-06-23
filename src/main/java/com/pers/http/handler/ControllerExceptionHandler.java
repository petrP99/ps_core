package com.pers.http.handler;

import com.pers.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.pers.http.rest")
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException exception) {
        String detail = messageSource.getMessage(
                exception.getErrorCode().getKey(),
                exception.getMessageArguments(),
                exception.getErrorCode().getKey(),
                LocaleContextHolder.getLocale()
        );
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(exception.getStatus(), detail);
        problem.setProperty("code", exception.getErrorCode().name());

        return ResponseEntity
                .status(exception.getStatus())
                .body(problem);
    }

}
