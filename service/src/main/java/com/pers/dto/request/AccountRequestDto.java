package com.pers.dto.request;

import com.pers.enums.Currency;
import jakarta.validation.constraints.Size;

public record AccountRequestDto(
        Currency currency,
        @Size(max = 50)
        String name
) {
}