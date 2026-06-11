package com.pers.exception;

import org.springframework.http.HttpStatus;

public class AccountException extends BusinessException {

    public AccountException(HttpStatus status, ErrorCode errorCode, Object... messageArguments) {
        super(status, errorCode, messageArguments);
    }
}
