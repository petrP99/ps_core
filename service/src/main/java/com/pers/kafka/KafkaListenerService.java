package com.pers.kafka;

import com.pers.dto.request.TransferRequestDto;
import com.pers.dto.event.AccountCloseEvent;
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

    @KafkaListener(topics = "${spring.kafka.topics.transfer-create}", groupId = "ps-group")
    public void listenTransferStatus(TransferRequestDto transfer) throws InterruptedException {
        log.info("Received transfer status event: {}", transfer);
        Thread.sleep(5000); // todo
        transferService.completeTransfer(transfer);
    }

    @KafkaListener(topics = "${spring.kafka.topics.account-close}", groupId = "ps-account-close-group")
    public void listenAccountClose(AccountCloseEvent event) {
        log.info("Received account close event: {}", event);
        accountClosureService.completeClosure(event);
    }
}
