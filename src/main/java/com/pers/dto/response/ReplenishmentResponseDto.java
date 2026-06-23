package com.pers.dto.response;

import com.pers.enums.Currency;
import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReplenishmentResponseDto(
        UUID id,
        UUID clientId,
        UUID accountId,
        String accountName,
        Currency currency,
        BigDecimal amount,
        LocalDateTime timeOfReplenishment,
        Status status
) {
}
