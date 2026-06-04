package com.pers.dto;

import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReplenishmentReadDto(
        Long id,
        UUID clientId,
        Long cardNo,
        BigDecimal amount,
        LocalDateTime timeOfReplenishment,
        Status status

) {
}