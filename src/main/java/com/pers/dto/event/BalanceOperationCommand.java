package com.pers.dto.event;

import com.pers.enums.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceOperationCommand(
        UUID operationId,
        UUID fromClientId,
        UUID toClientId,
        String cardFrom,
        String cardTo,
        BigDecimal debitAmount,
        BigDecimal amountTo,
        Currency currency,
        Currency targetCurrency
) {
}
