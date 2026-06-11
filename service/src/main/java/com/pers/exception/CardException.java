package com.pers.exception;

import org.springframework.http.HttpStatus;

public class CardException extends BusinessException {

    public CardException(HttpStatus status, ErrorCode errorCode, Object... messageArguments) {
        super(status, errorCode, messageArguments);
    }
}
