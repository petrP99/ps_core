package com.pers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pers.dto.event.BalanceOperationResult;
import com.pers.dto.event.PaymentCompletedEvent;
import com.pers.entity.OutboxEvent;
import com.pers.enums.OutboxEventType;
import com.pers.http.config.OutboxTraceContext;
import com.pers.kafka.KafkaProducerService;
import com.pers.repository.OutboxEventRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisherService {

    private static final int MAX_ERROR_LENGTH = 1000;
    private static final long MAX_RETRY_DELAY_SECONDS = 3600;

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;
    private final OutboxTraceContext outboxTraceContext;
    private final Tracer tracer;

    @Value("${outbox.publisher.batch-size:50}")
    private int batchSize;

    @Value("${outbox.publisher.retry-delay-seconds:10}")
    private long retryDelaySeconds;

    @Value("${outbox.publisher.max-attempts:20}")
    private int maxAttempts;

    @Transactional
    public void publishBatch() {
        List<OutboxEvent> events = outboxEventRepository.findReadyForPublishing(batchSize);
        events.forEach(this::publish);
    }

    @Transactional
    public int deletePublishedBefore(LocalDateTime cutoff) {
        return outboxEventRepository.deletePublishedBefore(cutoff);
    }

    private void publish(com.pers.entity.OutboxEvent event) {
        Span span = outboxTraceContext.startSpan(event.getTraceParent(), "outbox publish " + event.getEventType());
        try {
            try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
                OutboxEventType eventType = Objects.requireNonNull(event.getEventType());
                if (eventType == OutboxEventType.BALANCE_OPERATION_RESULT) {
                    publishBalanceOperationResult(event);
                } else if (eventType == OutboxEventType.PAYMENT_COMPLETED) {
                    publishPaymentCompleted(event);
                }
            }
            event.setStatus(OutboxEventType.PUBLISHED);
            event.setPublishedAt(LocalDateTime.now());
            event.setLastError(null);
        } catch (Exception e) {
            span.error(e);
            scheduleRetry(event, e);
        } finally {
            span.end();
        }
    }

    private void publishBalanceOperationResult(
            com.pers.entity.OutboxEvent event
    ) throws JsonProcessingException {
        BalanceOperationResult payload =
                objectMapper.readValue(event.getPayload(), BalanceOperationResult.class);
        kafkaProducerService.sendBalanceOperationResult(payload);
    }

    private void publishPaymentCompleted(com.pers.entity.OutboxEvent event) throws JsonProcessingException {
        PaymentCompletedEvent payload =
                objectMapper.readValue(event.getPayload(), PaymentCompletedEvent.class);
        kafkaProducerService.sendPaymentCompleted(payload);
    }

    private void scheduleRetry(com.pers.entity.OutboxEvent event, Exception exception) {
        int attempts = event.getAttempts() + 1;
        event.setAttempts(attempts);
        event.setLastError(limitErrorMessage(exception));

        if (attempts >= maxAttempts) {
            event.setStatus(OutboxEventType.FAILED);
            log.error("Outbox event {} reached max attempts", event.getId(), exception);
            return;
        }

        long multiplier = 1L << Math.min(attempts - 1, 6);
        long delaySeconds = Math.min(retryDelaySeconds * multiplier, MAX_RETRY_DELAY_SECONDS);
        event.setNextAttemptAt(LocalDateTime.now().plusSeconds(delaySeconds));
        log.info("Outbox event {} publish failed, retry {} scheduled in {} seconds",
                event.getId(), attempts, delaySeconds);
    }

    private String limitErrorMessage(Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = exception.getClass().getName();
        }
        return message.substring(0, Math.min(message.length(), MAX_ERROR_LENGTH));
    }
}
