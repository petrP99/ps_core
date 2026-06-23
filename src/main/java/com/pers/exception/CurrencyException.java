package com.pers.exception;

import org.springframework.http.HttpStatus;

public class CurrencyException extends BusinessException {

    public CurrencyException(HttpStatus status, ErrorCode errorCode, Object... messageArguments) {
        super(status, errorCode, messageArguments);
    }
}
