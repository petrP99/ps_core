package com.pers.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransferPreviewRequestDto(
        @NotBlank
        @Pattern(regexp = "\\d{16}", message = "Номер карты отправителя должен содержать 16 цифр")
        String cardFrom,

        @NotBlank
        @Pattern(regexp = "\\d{16}", message = "Номер карты получателя должен содержать 16 цифр")
        String cardTo,

        @NotNull
        @Positive
        BigDecimal amount,

        @Size(max = 120)
        String message
) {
}
