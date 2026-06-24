package com.pers.kafka;

import com.pers.dto.event.AccountCloseEvent;
import com.pers.dto.event.BalanceOperationResult;
import com.pers.dto.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.account-close}")
    private String accountCloseTopic;

    @Value("${spring.kafka.topics.balance-operation-result}")
    private String balanceOperationResultTopic;

    @Value("${spring.kafka.topics.notifications}")
    private String notificationsTopic;

    public void sendAccountCloseEvent(AccountCloseEvent event) {
        kafkaTemplate.send(accountCloseTopic, event.accountId().toString(), event);
        log.info("Отправлено событие по закрытию счете с accountId: {}", event.accountId());
    }

    public void sendBalanceOperationResult(BalanceOperationResult event) {
        try {
            kafkaTemplate.send(balanceOperationResultTopic, event.operationId().toString(), event)
                    .get(5, TimeUnit.SECONDS);
            log.info("Отправлен результат балансовой операции operationId={}", event.operationId());
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Не удалось опубликовать результат operationId=" + event.operationId(),
                    exception
            );
        }
    }

    public void sendNotification(NotificationEvent event) {
        kafkaTemplate.send(notificationsTopic, event.recipientPhone(), event);
        log.info("Отправлено уведомление {} для {}", event.type(), event.recipientPhone());
    }
}
