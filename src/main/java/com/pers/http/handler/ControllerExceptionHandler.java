package com.pers.http.handler;

import com.pers.exception.BusinessException;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.pers.http.controller")
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    private final MessageSource messageSource;
    private final Tracer tracer;

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
        addTraceId(problem);

        return ResponseEntity
                .status(exception.getStatus())
                .body(problem);
    }

    private void addTraceId(ProblemDetail problem) {
        Span span = tracer.currentSpan();
        if (span != null) {
            problem.setProperty("traceId", span.context().traceId());
        }
    }

}
