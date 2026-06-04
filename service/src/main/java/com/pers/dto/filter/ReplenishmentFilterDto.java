package com.pers.dto.filter;

import com.pers.enums.Status;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReplenishmentFilterDto(
        Long id,
        UUID clientId,
        Long cardNo,
        BigDecimal amount,
        LocalDateTime timeOfReplenishment,
        Status status
) {
}