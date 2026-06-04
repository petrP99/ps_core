package com.pers.dto.filter;

import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CardStatusDto(Long id,
                            UUID clientId,
                            UUID accountId,
                            BigDecimal balance,
                            LocalDate createdDate,
                            LocalDate expireDate,
                            Status status) {
}