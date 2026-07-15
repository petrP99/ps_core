package com.pers.dto.response;

import com.pers.enums.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountOperationContextResponse(
        UUID clientId,
        UUID accountFrom,
        String accountFromName,
        UUID accountTo,
        String accountToName,
        BigDecimal sourceBalance,
        Currency currency,
        Currency targetCurrency,
        BigDecimal sourceRate,
        BigDecimal targetRate
) {
}
