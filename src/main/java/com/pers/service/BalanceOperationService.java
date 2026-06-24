package com.pers.service;

import com.pers.dto.event.BalanceOperationCommand;
import com.pers.dto.event.BalanceOperationResult;
import com.pers.dto.response.CardResponseDto;
import com.pers.entity.Account;
import com.pers.entity.ProcessedBalanceOperation;
import com.pers.enums.Status;
import com.pers.exception.ErrorCode;
import com.pers.repository.AccountRepository;
import com.pers.repository.CardRepository;
import com.pers.repository.ProcessedBalanceOperationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceOperationService {

    private final ProcessedBalanceOperationRepository processedOperationRepository;
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final OutboxService outboxService;
    private final NotificationPublisherService notificationPublisherService;

    @Transactional
    public void execute(BalanceOperationCommand command) {
        if (processedOperationRepository.existsById(command.operationId())) {
            log.info("Балансовая операция {} уже обработана", command.operationId());
            return;
        }

        Optional<CardResponseDto> sourceCard = cardRepository.findByNumber(command.cardFrom());
        if (sourceCard.isEmpty()) {
            finish(command, false, ErrorCode.CARD_SENDER_NOT_FOUND);
            return;
        }
        Optional<CardResponseDto> targetCard = cardRepository.findByNumber(command.cardTo());
        if (targetCard.isEmpty()) {
            finish(command, false, ErrorCode.CARD_RECIPIENT_NOT_FOUND);
            return;
        }

        ErrorCode cardError = validateCards(command, sourceCard.get(), targetCard.get());
        if (cardError != null) {
            finish(command, false, cardError);
            return;
        }
        if (Objects.equals(sourceCard.get().accountId(), targetCard.get().accountId())) {
            finish(command, false, ErrorCode.OPERATION_SAME_ACCOUNT);
            return;
        }

        UUID firstId = sourceCard.get().accountId().compareTo(targetCard.get().accountId()) < 0
                ? sourceCard.get().accountId()
                : targetCard.get().accountId();
        UUID secondId = Objects.equals(firstId, sourceCard.get().accountId())
                ? targetCard.get().accountId()
                : sourceCard.get().accountId();

        Optional<Account> first = accountRepository.findByIdForUpdate(firstId);
        Optional<Account> second = accountRepository.findByIdForUpdate(secondId);
        if (first.isEmpty() || second.isEmpty()) {
            finish(command, false, ErrorCode.ACCOUNT_NOT_FOUND);
            return;
        }

        Account source = Objects.equals(first.get().getId(), sourceCard.get().accountId())
                ? first.get()
                : second.get();
        Account target = Objects.equals(first.get().getId(), targetCard.get().accountId())
                ? first.get()
                : second.get();

        ErrorCode accountError = validateAccounts(command, source, target);
        if (accountError != null) {
            finish(command, false, accountError);
            return;
        }
        if (source.getBalance().compareTo(command.debitAmount()) < 0) {
            finish(command, false, ErrorCode.ACCOUNT_INSUFFICIENT_FUNDS);
            return;
        }

        source.setBalance(source.getBalance().subtract(command.debitAmount()));
        target.setBalance(target.getBalance().add(command.amountTo()));
        finish(command, true, null);
        log.info("Балансовая операция {} выполнена", command.operationId());
    }

    private ErrorCode validateCards(BalanceOperationCommand command, CardResponseDto source, CardResponseDto target) {
        if (source.status() != Status.ACTIVE) {
            return ErrorCode.CARD_SENDER_UNAVAILABLE;
        }
        if (target.status() != Status.ACTIVE) {
            return ErrorCode.CARD_RECIPIENT_UNAVAILABLE;
        }
        if (!Objects.equals(source.clientId(), command.fromClientId())) {
            return ErrorCode.CARD_SENDER_NOT_OWNED;
        }
        if (!Objects.equals(target.clientId(), command.toClientId())) {
            return ErrorCode.CARD_RECIPIENT_UNAVAILABLE;
        }
        if (source.currency() != command.currency()
                || target.currency() != command.targetCurrency()) {
            return ErrorCode.CARD_ACCOUNT_MISMATCH;
        }
        return null;
    }

    private ErrorCode validateAccounts(BalanceOperationCommand command, Account source, Account target) {
        if (source.getStatus() != Status.ACTIVE) {
            return ErrorCode.ACCOUNT_CLOSED;
        }
        if (target.getStatus() != Status.ACTIVE) {
            return ErrorCode.ACCOUNT_RECIPIENT_UNAVAILABLE;
        }
        if (!Objects.equals(source.getClientId(), command.fromClientId())
                || source.getCurrency() != command.currency()) {
            return ErrorCode.CARD_ACCOUNT_MISMATCH;
        }
        if (!Objects.equals(target.getClientId(), command.toClientId())
                || target.getCurrency() != command.targetCurrency()) {
            return ErrorCode.ACCOUNT_RECIPIENT_UNAVAILABLE;
        }
        return null;
    }

    private void finish(BalanceOperationCommand command, boolean successful, ErrorCode failureCode) {
        UUID operationId = command.operationId();
        String failure = failureCode == null ? null : failureCode.name();
        processedOperationRepository.save(ProcessedBalanceOperation.builder()
                .operationId(operationId)
                .successful(successful)
                .failureCode(failure)
                .processedAt(LocalDateTime.now())
                .build());

        outboxService.saveBalanceOperationResult(
                new BalanceOperationResult(operationId, successful, failure)
        );

        if (successful) {
            notificationPublisherService.publish(
                    command.fromClientId(),
                    "TRANSFER_COMPLETED",
                    "Перевод выполнен",
                    "Списано " + command.debitAmount() + " " + command.currency(),
                    "ps-project",
                    operationId.toString()
            );
            notificationPublisherService.publish(
                    command.toClientId(),
                    "TRANSFER_RECEIVED",
                    "Перевод получен",
                    "Зачислено " + command.amountTo() + " " + command.targetCurrency(),
                    "ps-project",
                    operationId.toString()
            );
            return;
        }

        notificationPublisherService.publish(
                command.fromClientId(),
                "TRANSFER_FAILED",
                "Перевод не выполнен",
                "Перевод не выполнен: " + failure,
                "ps-project",
                operationId.toString()
        );
    }
}
