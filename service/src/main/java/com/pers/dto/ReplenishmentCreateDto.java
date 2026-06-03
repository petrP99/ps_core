package com.pers.dto;

import com.pers.validation.ReplenishmentInfo;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@ReplenishmentInfo
public record ReplenishmentCreateDto(
        @Positive
        BigDecimal amount,
        Long cardId
) {
}
