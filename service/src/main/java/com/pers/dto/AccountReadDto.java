package com.pers.dto;

import com.pers.enums.Currency;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AccountReadDto(
        UUID id,
        BigDecimal balance,
        Currency currency,
        String name,
        Integer cashback,
        List<CardReadDto> cards
) {
}