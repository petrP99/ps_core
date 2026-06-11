package com.pers.service;

import com.pers.dto.event.AccountCloseEvent;
import com.pers.entity.Account;
import com.pers.enums.Status;
import com.pers.exception.AccountException;
import com.pers.exception.ErrorCode;
import com.pers.kafka.KafkaProducerService;
import com.pers.repository.AccountRepository;
import com.pers.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
                .orElseThrow(() -> new AccountException(NOT_FOUND, ErrorCode.ACCOUNT_NOT_FOUND, accountId));

        if (account.getStatus() == Status.CLOSED) {
            throw new AccountException(CONFLICT, ErrorCode.ACCOUNT_ALREADY_CLOSED);
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountException(CONFLICT, ErrorCode.ACCOUNT_CLOSE_BALANCE_NOT_ZERO);
        }
        if (!closureCheckService.approve(account)) {
            throw new AccountException(CONFLICT, ErrorCode.ACCOUNT_CLOSE_CHECK_FAILED);
        }

        kafkaProducerService.sendAccountCloseEvent(
                new AccountCloseEvent(accountId, clientId, LocalDateTime.now())
        );
    }

    @Transactional
    public void completeClosure(AccountCloseEvent event) {
        accountRepository.findByIdAndClientId(event.accountId(), event.clientId())
                .ifPresentOrElse(
                        this::completeClosure,
                        () -> log.info("Счет из события закрытия не найден: {}", event)
                );
    }

    private void completeClosure(Account account) {
        if (account.getStatus() == Status.CLOSED) {
            log.info("Счет {} уже закрыт, повторное событие пропущено", account.getId());
            return;
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            log.info("Счет {} не закрыт: баланс изменился и равен {}", account.getId(), account.getBalance());
            return;
        }

        account.setStatus(Status.CLOSED);
        cardRepository.findAllByAccountId(account.getId())
                .forEach(card -> card.setStatus(Status.BLOCKED));
        log.info("Счет {} закрыт по событию Kafka", account.getId());
    }
}
