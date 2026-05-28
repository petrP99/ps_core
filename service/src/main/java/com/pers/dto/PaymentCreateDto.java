package com.pers.dto;

import com.pers.enums.Status;
import com.pers.validation.PaymentInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@PaymentInfo
public record PaymentCreateDto(
        @NotBlank
        String shopName,

        @Positive(message = "")
        @NotNull(message = "amount cant be empty")
        BigDecimal amount,

        Long clientId,

        Long cardId,

        @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
        LocalDateTime timeOfPay,
        Status status) {
}
