package com.pers.dto.event;

import java.util.UUID;

public record BalanceOperationResult(
        UUID operationId,
        boolean successful,
        String failureCode
) {
}
