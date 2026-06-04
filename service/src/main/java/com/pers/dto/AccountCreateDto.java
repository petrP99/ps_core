package com.pers.dto;

import com.pers.enums.Currency;
import jakarta.validation.constraints.Size;

public record AccountCreateDto(
        Currency currency,
        @Size(max = 50)
        String name
) {
}