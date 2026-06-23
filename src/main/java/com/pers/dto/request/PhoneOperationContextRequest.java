package com.pers.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PhoneOperationContextRequest(
        @NotBlank
        @Pattern(regexp = "\\d{16}", message = "{validation.card.sender.number}")
        String cardFrom,
        @NotBlank
        @Pattern(regexp = "8\\d{10}", message = "{validation.phone.number}")
        String phone
) {
}
