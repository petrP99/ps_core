package com.pers.dto.request;

import com.pers.enums.PaymentRecipient;
import com.pers.validation.PaymentInfo;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

@PaymentInfo
public record PaymentRequestDto(
        @NotBlank
        String paymentDestination,

        @Positive
        @NotNull
        @Digits(integer = 17, fraction = 2)
        BigDecimal amount,

        @NotNull
        UUID accountId,

        @NotNull
        PaymentRecipient recipient
) {
}
