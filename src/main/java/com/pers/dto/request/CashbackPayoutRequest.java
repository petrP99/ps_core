package com.pers.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CashbackPayoutRequest(
        @NotNull
        UUID operationId,

        @NotNull
        UUID clientId,

        @NotNull
        UUID accountId,

        @NotNull
        @Positive
        @Digits(integer = 17, fraction = 2)
        BigDecimal amount,

        @NotBlank
        String description
) {
}
