package com.pers.exception;

import org.springframework.http.HttpStatus;

public class PaymentException extends BusinessException {

    public PaymentException(HttpStatus status, ErrorCode errorCode, Object... messageArguments) {
        super(status, errorCode, messageArguments);
    }
}
