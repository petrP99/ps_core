package com.pers.kafka;

import com.pers.dto.event.AccountCloseEvent;
import com.pers.dto.request.TransferEventDto;
import com.pers.service.AccountClosureService;
import com.pers.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {

    private final TransferService transferService;
    private final AccountClosureService accountClosureService;

    @KafkaListener(topics = "${spring.kafka.topics.transfer-create}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenTransferStatus(TransferEventDto transfer) throws InterruptedException {
        log.info("Получено событие о переводе с transferId: {}", transfer.getId());
//        Thread.sleep(5000); // todo
        transferService.completeTransfer(transfer);
    }

    @KafkaListener(topics = "${spring.kafka.topics.account-close}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenAccountClose(AccountCloseEvent event) {
        log.info("Получено событие по закрытию счете с accountId: {}", event.accountId());
        accountClosureService.completeClosure(event);
    }

}

