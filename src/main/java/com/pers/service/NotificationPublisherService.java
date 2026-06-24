package com.pers.service;

import com.pers.dto.event.NotificationEvent;
import com.pers.kafka.KafkaProducerService;
import com.pers.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPublisherService {

    private final ClientRepository clientRepository;
    private final KafkaProducerService kafkaProducerService;

    public void publish(UUID clientId, String type, String title, String message, String source, String aggregateId) {
        clientRepository.findById(clientId)
                .map(client -> new NotificationEvent(
                        UUID.randomUUID(), client.getPhone(), type, title, message, source, aggregateId, LocalDateTime.now()))
                .ifPresentOrElse(
                        kafkaProducerService::sendNotification,
                        () -> log.warn("Notification skipped: client {} not found", clientId)
                );
    }
}
