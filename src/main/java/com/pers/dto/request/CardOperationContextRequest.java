package com.pers.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CardOperationContextRequest(
        @NotBlank
        @Pattern(regexp = "\\d{16}", message = "{validation.card.sender.number}")
        String cardFrom,
        @NotBlank
        @Pattern(regexp = "\\d{16}", message = "{validation.card.recipient.number}")
        String cardTo
) {
}
