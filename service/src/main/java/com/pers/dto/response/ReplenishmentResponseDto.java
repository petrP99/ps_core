package com.pers.dto.response;

import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReplenishmentResponseDto(
        Long id,
        UUID clientId,
        String cardNo,
        BigDecimal amount,
        LocalDateTime timeOfReplenishment,
        Status status

) {
}