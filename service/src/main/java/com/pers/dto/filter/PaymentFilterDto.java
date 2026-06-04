package com.pers.dto.filter;

import com.pers.enums.Status;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentFilterDto(
        Long id,
        String shopName,
        BigDecimal amount,
        UUID clientId,
        Long cardId,
        LocalDateTime timeOfPay,
        Status status
) {
}