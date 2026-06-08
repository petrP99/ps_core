package com.pers.kafka;

import com.pers.dto.request.TransferRequestDto;
import com.pers.dto.event.AccountCloseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.transfer-create}")
    private String transferCreateTopic;

    @Value("${spring.kafka.topics.account-close}")
    private String accountCloseTopic;

    public void sendTransferCreateEvent(TransferRequestDto dto) {
        kafkaTemplate.send(transferCreateTopic, dto.getFromClientId().toString(), dto);
        log.info("Sent transfer create event: {}", dto);
    }

    public void sendAccountCloseEvent(AccountCloseEvent event) {
        kafkaTemplate.send(accountCloseTopic, event.accountId().toString(), event).join();
        log.info("Sent account close event: {}", event);
    }
}
