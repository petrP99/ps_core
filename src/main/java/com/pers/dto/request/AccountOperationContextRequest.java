package com.pers.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AccountOperationContextRequest(
        @NotNull UUID accountFrom,
        @NotNull UUID accountTo
) {
}
