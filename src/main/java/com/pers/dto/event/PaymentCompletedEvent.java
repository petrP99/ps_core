package com.pers.dto.event;

import com.pers.enums.Currency;
import com.pers.enums.PaymentRecipient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCompletedEvent(
        UUID eventId,
        UUID paymentId,
        UUID clientId,
        UUID accountId,
        LocalDateTime accountCreatedAt,
        PaymentRecipient recipient,
        Currency currency,
        BigDecimal amount,
        String status,
        LocalDateTime occurredAt
) {
}
