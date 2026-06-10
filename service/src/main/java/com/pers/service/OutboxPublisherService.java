package com.pers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pers.dto.request.TransferEventDto;
import com.pers.entity.OutboxEvent;
import com.pers.enums.OutboxEventStatus;
import com.pers.kafka.KafkaProducerService;
import com.pers.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisherService {

    private static final int MAX_ERROR_LENGTH = 1000;
    private static final long MAX_RETRY_DELAY_SECONDS = 3600;

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;

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

    private void publish(OutboxEvent event) {
        try {
            switch (event.getEventType()) {
                case TRANSFER_CREATED -> publishTransferCreated(event);
            }
            event.setStatus(OutboxEventStatus.PUBLISHED);
            event.setPublishedAt(LocalDateTime.now());
            event.setLastError(null);
        } catch (Exception e) {
            scheduleRetry(event, e);
        }
    }

    private void publishTransferCreated(OutboxEvent event) throws JsonProcessingException {
        TransferEventDto payload = objectMapper.readValue(event.getPayload(), TransferEventDto.class);
        kafkaProducerService.sendTransferCreateEvent(event.getEventKey(), payload);
    }

    private void scheduleRetry(OutboxEvent event, Exception exception) {
        int attempts = event.getAttempts() + 1;
        event.setAttempts(attempts);
        event.setLastError(limitErrorMessage(exception));

        if (attempts >= maxAttempts) {
            event.setStatus(OutboxEventStatus.FAILED);
            log.error("Outbox event {} reached max attempts", event.getId(), exception);
            return;
        }

        long multiplier = 1L << Math.min(attempts - 1, 6);
        long delaySeconds = Math.min(retryDelaySeconds * multiplier, MAX_RETRY_DELAY_SECONDS);
        event.setNextAttemptAt(LocalDateTime.now().plusSeconds(delaySeconds));
        log.warn("Outbox event {} publish failed, retry {} scheduled in {} seconds",
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
