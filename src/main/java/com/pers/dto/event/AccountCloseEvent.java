package com.pers.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record AccountCloseEvent(
        UUID accountId,
        UUID clientId,
        LocalDateTime requestedAt
) {
}
