package com.pers.kafka;

import com.pers.dto.request.TransferRequestDto;
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

    @KafkaListener(topics = "ps-transfer-create", groupId = "ps-group")
    public void listenTransferStatus(TransferRequestDto transfer) throws InterruptedException {
        log.info("Received transfer status event: {}", transfer);
        Thread.sleep(5000);
        transferService.completeTransfer(transfer);
    }
}
