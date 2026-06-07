package com.pers.dto.request;

import com.pers.enums.Currency;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CardRequestDto(
        @Size(max = 50)
        String name,
        Currency currency,
        Boolean isPremium,
        UUID accountId) {
}
