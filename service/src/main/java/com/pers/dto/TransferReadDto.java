package com.pers.dto;

import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record TransferReadDto(
        Long id,
        Long clientId,

        Long cardIdFrom,
        Long cardIdTo,
        BigDecimal amount,
        LocalDateTime timeOfTransfer,
        String recipient,
        String message,
        Status status
) {
}
