package com.pers.dto.response;

import com.pers.enums.Currency;

import java.math.BigDecimal;

public record TransferPreviewResponseDto(
        String cardFrom,
        String cardTo,
        BigDecimal amount,
        BigDecimal amountTo,
        BigDecimal exchangeRate,
        BigDecimal commissionPercent,
        BigDecimal commission,
        BigDecimal debitAmount,
        Currency currency,
        Currency targetCurrency,
        String recipient,
        String recipientPhone,
        String message
) {
}
