package com.pers.dto;

import com.pers.enums.Currency;
import com.pers.enums.Status;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CardUpdateBalanceDto(Long id,
                                   UUID clientId,
                                   UUID accountId,
                                   @PositiveOrZero
                                   BigDecimal balance,
                                   LocalDate createdDate,
                                   LocalDate expireDate,
                                   String name,
                                   Currency currency,
                                   Status status) {
}