package com.pers.dto.response;

import com.pers.enums.Currency;
import com.pers.enums.AccountStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AccountResponseDto(
        UUID id,
        BigDecimal balance,
        Currency currency,
        String name,
        Integer cashback,
        AccountStatus status,
        List<CardResponseDto> cards
) {
}
