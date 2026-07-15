package com.pers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pers.dto.event.BalanceOperationResult;
import com.pers.dto.event.PaymentCompletedEvent;
import com.pers.enums.OutboxEventType;
import com.pers.exception.BusinessException;
import com.pers.exception.ErrorCode;
import com.pers.http.config.OutboxTraceContext;
import com.pers.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final OutboxTraceContext outboxTraceContext;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveBalanceOperationResult(BalanceOperationResult event) {
        save(event.operationId(), OutboxEventType.BALANCE_OPERATION_RESULT, event.operationId().toString(), event);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void savePaymentCompleted(PaymentCompletedEvent event) {
        save(event.paymentId(), OutboxEventType.PAYMENT_COMPLETED, event.paymentId().toString(), event);
    }

    private void save(UUID aggregateId, OutboxEventType eventType, String eventKey, Object payload) {
        LocalDateTime now = LocalDateTime.now();

        com.pers.entity.OutboxEvent outboxEvent = com.pers.entity.OutboxEvent.builder()
                .aggregateId(aggregateId)
                .eventType(eventType)
                .eventKey(eventKey)
                .payload(writePayload(payload))
                .traceParent(outboxTraceContext.captureTraceParent())
                .status(OutboxEventType.PENDING)
                .attempts(0)
                .createdAt(now)
                .nextAttemptAt(now)
                .build();

        outboxEventRepository.save(outboxEvent);
    }

    private String writePayload(Object event) {
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
