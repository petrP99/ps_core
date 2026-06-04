package com.pers.kafka;

import com.pers.dto.TransferCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTransferCreateEvent(TransferCreateDto dto) {
        kafkaTemplate.send("ps-transfer-create", dto.getFromClientId().toString(), dto);
        log.info("Sent transfer create event: {}", dto);
    }

}
