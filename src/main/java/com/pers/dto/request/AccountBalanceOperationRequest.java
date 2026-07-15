package com.pers.dto.request;

import com.pers.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountBalanceOperationRequest(
        @NotNull UUID accountFrom,
        @NotNull UUID accountTo,
        @NotNull @Positive BigDecimal debitAmount,
        @NotNull @Positive BigDecimal creditAmount,
        @NotNull Currency currency,
        @NotNull Currency targetCurrency
) {
}
