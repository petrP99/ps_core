package com.pers.service;

import com.pers.dto.request.PhoneTransferPreviewRequestDto;
import com.pers.dto.request.PhoneTransferRequestDto;
import com.pers.dto.request.TransferEventDto;
import com.pers.dto.request.TransferPreviewRequestDto;
import com.pers.dto.request.TransferRequestDto;
import com.pers.dto.response.CardResponseDto;
import com.pers.dto.response.TransferHistoryResponseDto;
import com.pers.dto.response.TransferPreviewResponseDto;
import com.pers.dto.response.TransferResponseDto;
import com.pers.entity.Account;
import com.pers.entity.AccountTransfer;
import com.pers.entity.Client;
import com.pers.entity.Transfer;
import com.pers.enums.Status;
import com.pers.exception.ErrorCode;
import com.pers.exception.TransferException;
import com.pers.mapper.TransferCreateMapper;
import com.pers.mapper.TransferReadMapper;
import com.pers.repository.AccountRepository;
import com.pers.repository.AccountTransferRepository;
import com.pers.repository.CardRepository;
import com.pers.repository.ClientRepository;
import com.pers.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.pers.enums.Currency.RUB;
import static com.pers.enums.Status.FAILED;
import static com.pers.enums.Status.IN_PROGRESS;
import static com.pers.enums.Status.SUCCESS;
import static com.pers.exception.ErrorCode.ACCOUNT_RECIPIENT_UNAVAILABLE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final TransferRepository transferRepository;
    private final TransferReadMapper transferReadMapper;
    private final TransferCreateMapper transferCreateMapper;
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final AccountTransferRepository accountTransferRepository;
    private final ClientRepository clientRepository;
    private final CurrencyService currencyService;
    private final OutboxService outboxService;

    @Value("${value.comission}")
    private BigDecimal commissionPercent;

    @Transactional
    public TransferResponseDto checkAndCreateTransfer(TransferRequestDto transferDto) {
        TransferContext context = validateTransfer(
                transferDto.getCardFrom(),
                transferDto.getCardTo(),
                transferDto.getAmount(),
                transferDto.getFromClientId(),
                true
        );

        context.accountFrom().setBalance(
                context.accountFrom().getBalance().subtract(context.calculation().debitAmount())
        );

        transferDto.setToClientId(context.cardTo().clientId());
        transferDto.setStatus(IN_PROGRESS);
        transferDto.setCurrency(context.accountFrom().getCurrency());
        transferDto.setTargetCurrency(context.accountTo().getCurrency());
        transferDto.setAmountTo(context.calculation().amountTo());
        transferDto.setExchangeRate(context.calculation().exchangeRate());
        transferDto.setCommission(context.calculation().commission());
        transferDto.setDebitAmount(context.calculation().debitAmount());
        transferDto.setRecipient(context.recipient());

        Transfer transfer = transferRepository.save(transferCreateMapper.toEntity(transferDto));
        TransferEventDto eventDto = transferCreateMapper.toEventDto(transfer);
        outboxService.saveTransferCreatedEvent(eventDto);
        log.info("Перевод {} на сумму {} создан", transfer.getId(), transferDto.getAmount());
        return transferReadMapper.toEntity(transfer);
    }

    @Transactional(readOnly = true)
    public TransferPreviewResponseDto previewTransfer(TransferPreviewRequestDto preview, UUID clientId) {
        TransferContext context = validateTransfer(
                preview.cardFrom(),
                preview.cardTo(),
                preview.amount(),
                clientId,
                false
        );

        return new TransferPreviewResponseDto(
                preview.cardFrom(),
                preview.cardTo(),
                preview.amount(),
                context.calculation().amountTo(),
                context.calculation().exchangeRate(),
                context.calculation().commissionPercent(),
                context.calculation().commission(),
                context.calculation().debitAmount(),
                context.accountFrom().getCurrency(),
                context.accountTo().getCurrency(),
                context.recipient(),
                null,
                preview.message()
        );
    }

    @Transactional(readOnly = true)
    public TransferPreviewResponseDto previewPhoneTransfer(PhoneTransferPreviewRequestDto preview, UUID clientId) {
        CardResponseDto cardTo = resolvePhoneRecipientCard(preview.phone(), preview.cardFrom(), clientId);
        TransferContext context = validateTransfer(preview.cardFrom(), cardTo.cardNumber(),
                preview.amount(), clientId, false);

        return new TransferPreviewResponseDto(
                preview.cardFrom(),
                cardTo.cardNumber(),
                preview.amount(),
                context.calculation().amountTo(),
                context.calculation().exchangeRate(),
                context.calculation().commissionPercent(),
                context.calculation().commission(),
                context.calculation().debitAmount(),
                context.accountFrom().getCurrency(),
                context.accountTo().getCurrency(),
                context.recipient(),
                preview.phone(),
                preview.message()
        );
    }

    @Transactional
    public TransferResponseDto checkAndCreatePhoneTransfer(PhoneTransferRequestDto phoneTransfer, UUID clientId) {
        CardResponseDto cardTo = resolvePhoneRecipientCard(
                phoneTransfer.phone(),
                phoneTransfer.cardFrom(),
                clientId
        );
        TransferRequestDto transfer = TransferRequestDto.builder()
                .fromClientId(clientId)
                .cardFrom(phoneTransfer.cardFrom())
                .cardTo(cardTo.cardNumber())
                .amount(phoneTransfer.amount())
                .recipientPhone(phoneTransfer.phone())
                .message(phoneTransfer.message())
                .build();
        return checkAndCreateTransfer(transfer);
    }

    @Transactional
    public void completeTransfer(TransferEventDto event) {
        Transfer transfer = transferRepository.findByIdForUpdate(event.getId())
                .orElseThrow(() -> new TransferException(
                        INTERNAL_SERVER_ERROR,
                        ErrorCode.TRANSFER_NOT_FOUND,
                        event.getId()
                ));

        if (transfer.getStatus() != IN_PROGRESS) {
            log.info("Перевод {} уже обработан со статусом {}", transfer.getId(), transfer.getStatus());
            return;
        }

        Optional<CardResponseDto> cardTo = cardRepository.findByNumber(transfer.getCardTo());
        if (cardTo.isEmpty() || cardTo.get().status() != Status.ACTIVE) {
            failTransfer(transfer, ErrorCode.CARD_RECIPIENT_UNAVAILABLE.name());
            return;
        }

        Optional<Account> accountTo = accountRepository.findByIdForUpdate(cardTo.get().accountId());
        if (accountTo.isEmpty()
                || accountTo.get().getStatus() != Status.ACTIVE
                || accountTo.get().getCurrency() != transfer.getTargetCurrency()
                || !Objects.equals(accountTo.get().getClientId(), transfer.getToClientId())) {
            failTransfer(transfer, ACCOUNT_RECIPIENT_UNAVAILABLE.name());
            return;
        }

        accountTo.get().setBalance(accountTo.get().getBalance().add(transfer.getAmountTo()));
        transfer.setStatus(SUCCESS);
        log.info("Перевод {} на сумму {} успешно выполнен", transfer.getId(), transfer.getAmount());
    }

    private void failTransfer(Transfer transfer, String reason) {
        CardResponseDto cardFrom = cardRepository.findByNumber(transfer.getCardFrom())
                .orElseThrow(() -> new TransferException(
                        INTERNAL_SERVER_ERROR,
                        ErrorCode.TRANSFER_REFUND_SENDER_CARD_NOT_FOUND
                ));
        Account accountFrom = accountRepository.findByIdForUpdate(cardFrom.accountId())
                .orElseThrow(() -> new TransferException(
                        INTERNAL_SERVER_ERROR,
                        ErrorCode.TRANSFER_REFUND_SENDER_ACCOUNT_NOT_FOUND
                ));

        BigDecimal refundAmount = transfer.getDebitAmount() != null
                ? transfer.getDebitAmount()
                : transfer.getAmount();
        accountFrom.setBalance(accountFrom.getBalance().add(refundAmount));
        transfer.setStatus(FAILED);
        log.error("Перевод {} отклонен: {}", transfer.getId(), reason);
    }

    private CardResponseDto findCard(String cardNumber, ErrorCode errorCode) {
        return cardRepository.findByNumber(cardNumber)
                .orElseThrow(() -> new TransferException(NOT_FOUND, errorCode, cardNumber));
    }

    private void validateActiveCard(CardResponseDto card, ErrorCode errorCode) {
        if (card.status() != Status.ACTIVE) {
            throw new TransferException(CONFLICT, errorCode);
        }
    }

    private void validateAccount(Account account, CardResponseDto card) {
        if (account.getStatus() != Status.ACTIVE) {
            throw new TransferException(CONFLICT, ErrorCode.ACCOUNT_CLOSED);
        }
        if (!Objects.equals(account.getClientId(), card.clientId())
                || account.getCurrency() != card.currency()) {
            throw new TransferException(CONFLICT, ErrorCode.CARD_ACCOUNT_MISMATCH);
        }
    }

    private CardResponseDto resolvePhoneRecipientCard(
            String phone,
            String cardFromNumber,
            UUID clientId
    ) {
        CardResponseDto cardFrom = findCard(cardFromNumber, ErrorCode.CARD_SENDER_NOT_FOUND);
        if (!Objects.equals(cardFrom.clientId(), clientId)) {
            throw new TransferException(FORBIDDEN, ErrorCode.CARD_SENDER_NOT_OWNED);
        }
        validateActiveCard(cardFrom, ErrorCode.CARD_SENDER_UNAVAILABLE);
        if (cardFrom.currency() != RUB) {
            throw new TransferException(BAD_REQUEST, ErrorCode.TRANSFER_PHONE_RUB_ONLY);
        }

        Client recipient = clientRepository.findByPhone(phone)
                .orElseThrow(() -> new TransferException(
                        NOT_FOUND,
                        ErrorCode.TRANSFER_RECIPIENT_PHONE_NOT_FOUND,
                        phone
                ));
        if (recipient.getStatus() == Status.BLOCKED) {
            throw new TransferException(CONFLICT, ErrorCode.TRANSFER_RECIPIENT_BLOCKED);
        }
        if (recipient.getStatus() != Status.ACTIVE) {
            throw new TransferException(CONFLICT, ErrorCode.TRANSFER_RECIPIENT_UNAVAILABLE);
        }

        List<CardResponseDto> recipientCards = cardRepository.findCardsWithBalanceByClientId(recipient.getId());
        return recipientCards.stream()
                .filter(card -> card.status() == Status.ACTIVE)
                .filter(card -> card.currency() == RUB)
                .filter(card -> !Objects.equals(card.accountId(), cardFrom.accountId()))
                .sorted(Comparator.comparing(CardResponseDto::cardNumber))
                .findAny()
                .orElseThrow(() -> new TransferException(
                        CONFLICT,
                        ErrorCode.TRANSFER_RECIPIENT_RUB_CARD_UNAVAILABLE
                ));
    }

    private TransferContext validateTransfer(
            String cardFromNumber,
            String cardToNumber,
            BigDecimal amount,
            UUID clientId,
            boolean lockSourceAccount
    ) {
        if (Objects.equals(cardFromNumber, cardToNumber)) {
            throw new TransferException(BAD_REQUEST, ErrorCode.TRANSFER_SAME_CARD);
        }

        CardResponseDto cardFrom = findCard(cardFromNumber, ErrorCode.CARD_SENDER_NOT_FOUND);
        CardResponseDto cardTo = findCard(cardToNumber, ErrorCode.CARD_RECIPIENT_NOT_FOUND);

        if (!Objects.equals(cardFrom.clientId(), clientId)) {
            throw new TransferException(FORBIDDEN, ErrorCode.CARD_SENDER_NOT_OWNED);
        }
        validateActiveCard(cardFrom, ErrorCode.CARD_SENDER_UNAVAILABLE);
        validateActiveCard(cardTo, ErrorCode.CARD_RECIPIENT_UNAVAILABLE);

        if (Objects.equals(cardFrom.accountId(), cardTo.accountId())) {
            throw new TransferException(BAD_REQUEST, ErrorCode.TRANSFER_SAME_ACCOUNT);
        }

        Account accountFrom = (lockSourceAccount
                ? accountRepository.findByIdForUpdate(cardFrom.accountId())
                : accountRepository.findById(cardFrom.accountId()))
                .orElseThrow(() -> new TransferException(
                        NOT_FOUND,
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        cardFrom.accountId()
                ));
        Account accountTo = accountRepository.findById(cardTo.accountId())
                .orElseThrow(() -> new TransferException(
                        NOT_FOUND,
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        cardTo.accountId()
                ));

        validateAccount(accountFrom, cardFrom);
        validateAccount(accountTo, cardTo);
        TransferCalculation calculation = calculateTransfer(
                amount,
                accountFrom.getCurrency(),
                accountTo.getCurrency()
        );
        if (accountFrom.getBalance().compareTo(calculation.debitAmount()) < 0) {
            throw new TransferException(CONFLICT, ErrorCode.ACCOUNT_INSUFFICIENT_FUNDS);
        }

        Client recipient = clientRepository.findById(cardTo.clientId())
                .orElseThrow(() -> new TransferException(
                        NOT_FOUND,
                        ErrorCode.TRANSFER_RECIPIENT_NOT_FOUND,
                        cardTo.clientId()
                ));
        if (recipient.getStatus() != Status.ACTIVE) {
            throw new TransferException(CONFLICT, ErrorCode.TRANSFER_RECIPIENT_UNAVAILABLE);
        }
        String recipientName = (
                Objects.toString(recipient.getFirstName(), "")
                        + " "
                        + Objects.toString(recipient.getLastName(), "")
        ).trim();
        if (recipientName.isBlank()) {
            throw new TransferException(CONFLICT, ErrorCode.TRANSFER_RECIPIENT_NAME_MISSING);
        }

        return new TransferContext(
                cardFrom,
                cardTo,
                accountFrom,
                accountTo,
                recipientName,
                calculation
        );
    }

    private TransferCalculation calculateTransfer(
            BigDecimal amount,
            com.pers.enums.Currency sourceCurrency,
            com.pers.enums.Currency targetCurrency
    ) {
        CurrencyService.ConversionResult conversion = currencyService.convert(
                amount,
                sourceCurrency,
                targetCurrency
        );
        boolean isExchange = sourceCurrency != targetCurrency;
        BigDecimal appliedCommissionPercent = isExchange
                ? commissionPercent
                : BigDecimal.ZERO;
        BigDecimal commission = amount
                .multiply(appliedCommissionPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal debitAmount = amount.add(commission).setScale(2, RoundingMode.HALF_UP);

        return new TransferCalculation(
                conversion.convertedAmount(),
                conversion.exchangeRate(),
                appliedCommissionPercent,
                commission,
                debitAmount
        );
    }

    private record TransferContext(
            CardResponseDto cardFrom,
            CardResponseDto cardTo,
            Account accountFrom,
            Account accountTo,
            String recipient,
            TransferCalculation calculation
    ) {
    }

    private record TransferCalculation(
            BigDecimal amountTo,
            BigDecimal exchangeRate,
            BigDecimal commissionPercent,
            BigDecimal commission,
            BigDecimal debitAmount
    ) {
    }

    public Page<TransferHistoryResponseDto> findHistoryByClient(
            Pageable pageable,
            UUID clientId
    ) {
        List<TransferHistoryResponseDto> history = new ArrayList<>();
        transferRepository.findAllByParticipantOrderByTimeOfTransferDesc(clientId).stream()
                .map(transfer -> toHistoryDto(transfer, clientId))
                .forEach(history::add);
        accountTransferRepository.findAllByClientIdOrderByTimeOfTransferDesc(clientId).stream()
                .map(this::toHistoryDto)
                .forEach(history::add);
        history.sort(Comparator.comparing(
                TransferHistoryResponseDto::timeOfTransfer,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));

        int start = Math.min((int) pageable.getOffset(), history.size());
        int end = Math.min(start + pageable.getPageSize(), history.size());
        return new PageImpl<>(history.subList(start, end), pageable, history.size());
    }

    public Optional<TransferHistoryResponseDto> findHistoryByIdAndClientId(
            UUID id,
            UUID clientId
    ) {
        Optional<TransferHistoryResponseDto> cardTransfer = transferRepository
                .findByIdAndParticipant(id, clientId)
                .map(transfer -> toHistoryDto(transfer, clientId));
        if (cardTransfer.isPresent()) {
            return cardTransfer;
        }
        return accountTransferRepository.findByIdAndClientId(id, clientId)
                .map(this::toHistoryDto);
    }

    private TransferHistoryResponseDto toHistoryDto(Transfer transfer, UUID clientId) {
        boolean incoming = Objects.equals(transfer.getToClientId(), clientId);
        UUID counterpartyId = incoming ? transfer.getFromClientId() : transfer.getToClientId();
        String counterparty = clientRepository.findFirstAndLastNameByClientId(counterpartyId);

        return new TransferHistoryResponseDto(
                transfer.getId(),
                incoming,
                counterparty,
                transfer.getCardFrom(),
                transfer.getCardTo(),
                transfer.getAmount(),
                transfer.getAmountTo(),
                transfer.getTimeOfTransfer(),
                transfer.getRecipientPhone(),
                transfer.getMessage(),
                transfer.getStatus(),
                transfer.getExchangeRate(),
                transfer.getCommission(),
                transfer.getDebitAmount(),
                transfer.getCurrency(),
                transfer.getTargetCurrency(),
                "CARD",
                null,
                null,
                null,
                null
        );
    }

    private TransferHistoryResponseDto toHistoryDto(AccountTransfer transfer) {
        return new TransferHistoryResponseDto(
                transfer.getId(),
                false,
                "Между своими счетами",
                null,
                null,
                transfer.getAmount(),
                transfer.getAmountTo(),
                transfer.getTimeOfTransfer(),
                null,
                null,
                SUCCESS,
                transfer.getExchangeRate(),
                BigDecimal.ZERO,
                transfer.getAmount(),
                transfer.getCurrency(),
                transfer.getTargetCurrency(),
                "ACCOUNT",
                transfer.getAccountFrom(),
                transfer.getAccountFromName(),
                transfer.getAccountTo(),
                transfer.getAccountToName()
        );
    }

    public Optional<TransferResponseDto> findByIdAndClientId(UUID id, UUID clientId) {
        return transferRepository.findByIdAndFromClientId(id, clientId)
                .map(transferReadMapper::toEntity);
    }

}
