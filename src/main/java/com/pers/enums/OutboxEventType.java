package com.pers.enums;

public enum OutboxEventType {
    PENDING,
    PUBLISHED,
    FAILED,
    BALANCE_OPERATION_RESULT,
    PAYMENT_COMPLETED
}
