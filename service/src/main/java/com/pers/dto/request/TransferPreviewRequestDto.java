package com.pers.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransferPreviewRequestDto(
        @NotBlank
        @Pattern(regexp = "\\d{16}", message = "{validation.card.sender.number}")
        String cardFrom,

        @NotBlank
        @Pattern(regexp = "\\d{16}", message = "{validation.card.recipient.number}")
        String cardTo,

        @NotNull
        @Positive
        BigDecimal amount,

        @Size(max = 120)
        String message
) {
}
