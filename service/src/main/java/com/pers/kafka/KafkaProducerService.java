package com.pers.kafka;

import com.pers.dto.event.AccountCloseEvent;
import com.pers.dto.request.TransferEventDto;
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

    public void sendTransferCreateEvent(String eventKey, TransferEventDto dto) {
        kafkaTemplate.send(transferCreateTopic, eventKey, dto);
        log.info("Отправлено событие о переводе с transferId: {}", dto.getId());
    }

    public void sendAccountCloseEvent(AccountCloseEvent event) {
        kafkaTemplate.send(accountCloseTopic, event.accountId().toString(), event);
        log.info("Отправлено событие по закрытию счете с accountId: {}", event.accountId());
    }
}
