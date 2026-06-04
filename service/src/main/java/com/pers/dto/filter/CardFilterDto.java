package com.pers.dto.filter;


import com.pers.enums.Currency;
import com.pers.enums.Status;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CardFilterDto(
        Long id,
        UUID clientId,
        UUID accountId,
        BigDecimal balance,
        LocalDate expireDate,
        String name,
        Currency currency,
        Status status) {
}