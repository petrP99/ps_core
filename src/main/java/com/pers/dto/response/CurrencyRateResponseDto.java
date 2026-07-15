package com.pers.dto.response;

import com.pers.enums.Currency;

import java.math.BigDecimal;

public record CurrencyRateResponseDto(
        Currency currency,
        BigDecimal rate
) {
}
