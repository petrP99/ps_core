package com.pers.exception;

import org.springframework.http.HttpStatus;

public class TransferException extends BusinessException {

    public TransferException(HttpStatus status, ErrorCode errorCode, Object... messageArguments) {
        super(status, errorCode, messageArguments);
    }
}
