package com.pers.dto.request;

import com.pers.enums.Status;
import com.pers.validation.PaymentInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@PaymentInfo
public record PaymentRequestDto(
        @NotBlank
        String shopName,

        @Positive(message = "")
        @NotNull(message = "amount cant be empty")
        BigDecimal amount,

        UUID clientId,

        String cardNo,

        @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
        LocalDateTime timeOfPay,
        Status status) {
}