package com.pers.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationEvent(
        UUID eventId,
        String recipientPhone,
        String type,
        String title,
        String message,
        String source,
        String aggregateId,
        LocalDateTime occurredAt
) {
}
