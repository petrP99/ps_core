package com.pers.dto.response;

import com.pers.enums.Currency;
import com.pers.enums.PaymentRecipient;
import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponseDto(
        UUID id,
        UUID clientId,
        UUID accountId,
        String accountName,
        Currency currency,
        PaymentRecipient recipient,
        String paymentDestination,
        BigDecimal amount,
        LocalDateTime timeOfPay,
        Status status
) {
}
