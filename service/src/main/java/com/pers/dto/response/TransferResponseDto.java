package com.pers.dto.response;

import com.pers.enums.Currency;
import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferResponseDto(
        UUID id,
        UUID fromClientId,
        UUID toClientId,
        String cardFrom,
        String cardTo,
        BigDecimal amount,
        LocalDateTime timeOfTransfer,
        String recipient,
        String recipientPhone,
        String message,
        Status status,
        BigDecimal amountTo,
        BigDecimal exchangeRate,
        BigDecimal commission,
        BigDecimal debitAmount,
        Currency currency,
        Currency targetCurrency,
        boolean isExchange
) {
}
