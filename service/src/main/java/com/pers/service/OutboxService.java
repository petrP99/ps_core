package com.pers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pers.dto.event.BalanceOperationResult;
import com.pers.enums.OutboxEventType;
import com.pers.exception.BusinessException;
import com.pers.exception.ErrorCode;
import com.pers.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveBalanceOperationResult(BalanceOperationResult event) {
        LocalDateTime now = LocalDateTime.now();

        com.pers.entity.OutboxEvent outboxEvent = com.pers.entity.OutboxEvent.builder()
                .aggregateId(event.operationId())
                .eventType(OutboxEventType.BALANCE_OPERATION_RESULT)
                .eventKey(event.operationId().toString())
                .payload(writePayload(event))
                .status(OutboxEventType.PENDING)
                .attempts(0)
                .createdAt(now)
                .nextAttemptAt(now)
                .build();

        outboxEventRepository.save(outboxEvent);
    }

    private String writePayload(BalanceOperationResult event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new BusinessException(
                    INTERNAL_SERVER_ERROR,
                    ErrorCode.OUTBOX_SERIALIZE_FAILED,
                    e
            );
        }
    }
}
