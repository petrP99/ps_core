package com.pers.exception;

import org.springframework.http.HttpStatus;

public class ReplenishmentException extends BusinessException {

    public ReplenishmentException(HttpStatus status, ErrorCode errorCode, Object... messageArguments) {
        super(status, errorCode, messageArguments);
    }
}
