package com.pers.dto;

import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record PaymentReadDto(Long id,
                             String shopName,
                             BigDecimal amount,
                             Long clientId,
                             Long cardId,
                             LocalDateTime timeOfPay,
                             Status status) {
}
