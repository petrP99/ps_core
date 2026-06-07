package com.pers.dto.response;

import com.pers.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponseDto(Long id,
                                 String shopName,
                                 BigDecimal amount,
                                 UUID clientId,
                                 String cardNo,
                                 LocalDateTime timeOfPay,
                                 Status status) {
}