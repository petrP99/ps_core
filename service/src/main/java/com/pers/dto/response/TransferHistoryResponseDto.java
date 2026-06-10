package com.pers.dto.response;

import com.pers.enums.Currency;
import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferHistoryResponseDto(
        UUID id,
        boolean incoming,
        String counterparty,
        String cardFrom,
        String cardTo,
        BigDecimal amount,
        BigDecimal amountTo,
        LocalDateTime timeOfTransfer,
        String recipientPhone,
        String message,
        Status status,
        BigDecimal exchangeRate,
        BigDecimal commission,
        BigDecimal debitAmount,
        Currency currency,
        Currency targetCurrency,
        String operationType,
        UUID accountFrom,
        String accountFromName,
        UUID accountTo,
        String accountToName
) {
}
