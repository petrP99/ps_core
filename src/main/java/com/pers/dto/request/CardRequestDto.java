package com.pers.dto.request;

import com.pers.enums.Currency;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.util.UUID;

public record CardRequestDto(
        @Size(max = 50)
        String name,
        @NonNull
        Currency currency,
        Boolean isPremium,
        @NonNull
        UUID accountId) {
}
