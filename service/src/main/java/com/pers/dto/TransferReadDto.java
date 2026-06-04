package com.pers.dto;

import com.pers.enums.Currency;
import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferReadDto(
        Long id,
        UUID fromClientId,
        UUID toClientId,
        Long cardIdFrom,
        Long cardIdTo,
        BigDecimal amount,
        LocalDateTime timeOfTransfer,
        String recipient,
        String message,
        Status status,
        BigDecimal amountTo,
        Currency currency,
        Currency targetCurrency,
        boolean isExchange
) {
}