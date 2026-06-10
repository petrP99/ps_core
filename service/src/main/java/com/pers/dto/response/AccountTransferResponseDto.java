package com.pers.dto.response;

import com.pers.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountTransferResponseDto(
        UUID id,
        UUID accountFrom,
        String accountFromName,
        UUID accountTo,
        String accountToName,
        BigDecimal amount,
        BigDecimal amountTo,
        BigDecimal exchangeRate,
        Currency currency,
        Currency targetCurrency,
        LocalDateTime timeOfTransfer
) {
}
