package com.pers.dto.filter;

import com.pers.enums.PaymentRecipient;
import com.pers.enums.Status;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentFilterDto(
        UUID id,
        String paymentDestination,
        BigDecimal amount,
        UUID clientId,
        UUID accountId,
        PaymentRecipient recipient,
        LocalDateTime timeOfPay,
        Status status
) {
}
