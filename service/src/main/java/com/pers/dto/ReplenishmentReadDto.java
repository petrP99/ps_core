package com.pers.dto;

import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record ReplenishmentReadDto(
        Long id,
        Long clientId,
        Long cardNo,
        BigDecimal amount,
        LocalDateTime timeOfReplenishment,
        Status status

) {
}
