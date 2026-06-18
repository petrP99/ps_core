package com.pers.kafka;

import com.pers.dto.event.AccountCloseEvent;
import com.pers.dto.event.BalanceOperationCommand;
import com.pers.service.AccountClosureService;
import com.pers.service.BalanceOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {

    private final BalanceOperationService balanceOperationService;
    private final AccountClosureService accountClosureService;

    @KafkaListener(
            topics = "${spring.kafka.topics.balance-operation-execute}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void executeBalanceOperation(BalanceOperationCommand command) {
        log.info("Получена команда балансовой операции operationId={}", command.operationId());
        balanceOperationService.execute(command);
    }

    @KafkaListener(topics = "${spring.kafka.topics.account-close}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenAccountClose(AccountCloseEvent event) {
        log.info("Получено событие по закрытию счете с accountId: {}", event.accountId());
        accountClosureService.completeClosure(event);
    }

}
