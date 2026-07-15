package com.pers.dto.response;

import java.util.List;

public record CurrencyRatesResponseDto(
        List<CurrencyRateResponseDto> rates
) {
}
