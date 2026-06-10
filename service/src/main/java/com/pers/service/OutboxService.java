package com.pers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pers.dto.request.TransferEventDto;
import com.pers.entity.OutboxEvent;
import com.pers.enums.OutboxEventStatus;
import com.pers.enums.OutboxEventType;
import com.pers.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveTransferCreatedEvent(TransferEventDto event) {
        LocalDateTime now = LocalDateTime.now();

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(event.getId())
                .eventType(OutboxEventType.TRANSFER_CREATED)
                .eventKey(event.getFromClientId().toString())
                .payload(writePayload(event))
                .status(OutboxEventStatus.PENDING)
                .attempts(0)
                .createdAt(now)
                .nextAttemptAt(now)
                .build();

        outboxEventRepository.save(outboxEvent);
    }

    private String writePayload(TransferEventDto event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось сериализовать событие перевода", e);
        }
    }
}
