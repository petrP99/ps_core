package com.pers.dto.filter;

import com.pers.enums.Status;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TransferFilterDto(
        Long id,
        UUID fromClientId,
        UUID toClientId,
        Long cardNoFrom,
        Long cardNoTo,
        BigDecimal amount,
        LocalDateTime timeOfTransfer,
        String recipient,
        String message,
        Status status
) {
}