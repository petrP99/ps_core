package com.pers.dto.request;

import com.pers.validation.ReplenishmentInfo;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

@ReplenishmentInfo
public record ReplenishmentRequestDto(
        @Positive
        BigDecimal amount,
        String cardNo
) {
}
