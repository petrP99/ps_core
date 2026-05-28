package com.pers.dto;

import com.pers.enums.Status;
import com.pers.validation.ReplenishmentInfo;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@ReplenishmentInfo
public record ReplenishmentCreateDto(
        Long clientId,
        Long cardId,

        @Positive
        BigDecimal amount,

        @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
        LocalDateTime timeOfReplenishment,
        Status status

) {
}
