package com.pers.service;

import com.pers.dto.event.AccountCloseEvent;
import com.pers.entity.Account;
import com.pers.enums.AccountStatus;
import com.pers.enums.Status;
import com.pers.kafka.KafkaProducerService;
import com.pers.repository.AccountRepository;
import com.pers.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountClosureService {

    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final AccountClosureCheckService closureCheckService;
    private final KafkaProducerService kafkaProducerService;

    @Transactional(readOnly = true)
    public void requestClosure(UUID accountId, UUID clientId) {
        Account account = accountRepository.findByIdAndClientId(accountId, clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счет не найден"));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Счет уже закрыт");
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Для закрытия счета баланс должен быть нулевым");
        }
        if (!closureCheckService.approve(account)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Проверка закрытия счета не пройдена");
        }

        kafkaProducerService.sendAccountCloseEvent(
                new AccountCloseEvent(accountId, clientId, LocalDateTime.now())
        );
    }

    @Transactional
    public void completeClosure(AccountCloseEvent event) {
        Account account = accountRepository.findByIdAndClientId(event.accountId(), event.clientId())
                .orElse(null);

        if (account == null) {
            log.warn("Счет из события закрытия не найден: {}", event);
            return;
        }
        if (account.getStatus() == AccountStatus.CLOSED) {
            log.info("Счет {} уже закрыт, повторное событие пропущено", account.getId());
            return;
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            log.warn("Счет {} не закрыт: баланс изменился и равен {}", account.getId(), account.getBalance());
            return;
        }

        account.setStatus(AccountStatus.CLOSED);
        cardRepository.findAllByAccountId(account.getId())
                .forEach(card -> card.setStatus(Status.BLOCKED));
        log.info("Счет {} закрыт по событию Kafka", account.getId());
    }
}
