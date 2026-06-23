package com.pers.dto.response;

import com.pers.enums.Currency;
import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CardResponseDto(
        UUID id,
        UUID clientId,
        UUID accountId,
        BigDecimal balance,
        LocalDate createdDate,
        LocalDate expireDate,
        String name,
        Currency currency,
        Status status,
        String cardNumber) {
}
