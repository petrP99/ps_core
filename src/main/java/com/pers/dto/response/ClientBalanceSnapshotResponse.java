package com.pers.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ClientBalanceSnapshotResponse(
        UUID clientId,
        BigDecimal totalBalance
) {
}
