package com.pers.dto;

import com.pers.enums.Currency;
import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardReadDto(Long id,
                          Long clientId,
                          BigDecimal balance,
                          LocalDate createdDate,
                          LocalDate expireDate,
                          String name,
                          Currency currency,
                          Status status) {
}
