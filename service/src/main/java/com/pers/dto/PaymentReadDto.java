package com.pers.dto;

import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentReadDto(Long id,
                             String shopName,
                             BigDecimal amount,
                             UUID clientId,
                             Long cardId,
                             LocalDateTime timeOfPay,
                             Status status) {
}