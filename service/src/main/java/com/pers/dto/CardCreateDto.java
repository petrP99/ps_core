package com.pers.dto;

import com.pers.enums.Currency;
import com.pers.enums.Status;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardCreateDto(Long clientId,
                            @PositiveOrZero
                            BigDecimal balance,
                            LocalDate createdDate,
                            LocalDate expireDate,
                        @Size(max = 50)
                            String name,
                            Currency currency,
                            Status status) {
}
