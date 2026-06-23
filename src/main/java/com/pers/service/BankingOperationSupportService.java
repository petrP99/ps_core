package com.pers.service;

import com.pers.dto.request.AccountBalanceOperationRequest;
import com.pers.dto.request.AccountOperationContextRequest;
import com.pers.dto.request.CardOperationContextRequest;
import com.pers.dto.request.PhoneOperationContextRequest;
import com.pers.dto.response.AccountOperationContextResponse;
import com.pers.dto.response.CardOperationContextResponse;
import com.pers.dto.response.CardResponseDto;
import com.pers.entity.Account;
import com.pers.entity.Client;
import com.pers.enums.Status;
import com.pers.exception.BusinessException;
import com.pers.exception.ErrorCode;
import com.pers.repository.AccountRepository;
import com.pers.repository.CardRepository;
import com.pers.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.pers.enums.Currency.RUB;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BankingOperationSupportService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final CurrencyService currencyService;

    @Value("${services.ps-transfer.internal-token}")
    private String internalToken;

    @Transactional(readOnly = true)
    public CardOperationContextResponse getCardContext(CardOperationContextRequest request, UUID clientId) {
        return buildCardContext(request.cardFrom(), request.cardTo(), clientId);
    }

    @Transactional(readOnly = true)
    public CardOperationContextResponse getPhoneContext(PhoneOperationContextRequest request, UUID clientId) {
        CardResponseDto recipientCard = resolvePhoneRecipientCard(request.phone(), request.cardFrom(), clientId);
        return buildCardContext(request.cardFrom(), recipientCard.cardNumber(), clientId);
    }

    @Transactional(readOnly = true)
    public AccountOperationContextResponse getAccountContext(AccountOperationContextRequest request, UUID clientId) {
        if (Objects.equals(request.accountFrom(), request.accountTo())) {
            throw new BusinessException(BAD_REQUEST, ErrorCode.ACCOUNT_SAME);
        }
        Account source = findClientAccount(request.accountFrom(), clientId);
        Account target = findClientAccount(request.accountTo(), clientId);
        validateActiveAccount(source);
        validateActiveAccount(target);
        return new AccountOperationContextResponse(
                clientId,
                source.getId(),
                source.getName(),
                target.getId(),
                target.getName(),
                source.getBalance(),
                source.getCurrency(),
                target.getCurrency(),
                currencyService.getRateFromCache(source.getCurrency()),
                currencyService.getRateFromCache(target.getCurrency())
        );
    }

    @Transactional
    public void executeAccountOperation(AccountBalanceOperationRequest request, UUID clientId) {
        if (Objects.equals(request.accountFrom(), request.accountTo())) {
            throw new BusinessException(BAD_REQUEST, ErrorCode.ACCOUNT_SAME);
        }

        UUID firstId = request.accountFrom().compareTo(request.accountTo()) < 0
                ? request.accountFrom()
                : request.accountTo();
        UUID secondId = Objects.equals(firstId, request.accountFrom())
                ? request.accountTo()
                : request.accountFrom();

        Account first = findAccountForUpdate(firstId);
        Account second = findAccountForUpdate(secondId);
        Account source = Objects.equals(first.getId(), request.accountFrom()) ? first : second;
        Account target = Objects.equals(first.getId(), request.accountTo()) ? first : second;

        validateOwnedAccount(source, clientId);
        validateOwnedAccount(target, clientId);
        validateActiveAccount(source);
        validateActiveAccount(target);
        if (source.getCurrency() != request.currency()
                || target.getCurrency() != request.targetCurrency()) {
            throw new BusinessException(CONFLICT, ErrorCode.CARD_ACCOUNT_MISMATCH);
        }
        if (source.getBalance().compareTo(request.debitAmount()) < 0) {
            throw new BusinessException(CONFLICT, ErrorCode.ACCOUNT_INSUFFICIENT_FUNDS);
        }

        source.setBalance(source.getBalance().subtract(request.debitAmount()));
        target.setBalance(target.getBalance().add(request.creditAmount()));
    }

    private CardOperationContextResponse buildCardContext(String sourceNumber, String targetNumber, UUID clientId) {
        if (Objects.equals(sourceNumber, targetNumber)) {
            throw new BusinessException(BAD_REQUEST, ErrorCode.OPERATION_SAME_CARD);
        }

        CardResponseDto sourceCard = findCard(sourceNumber, ErrorCode.CARD_SENDER_NOT_FOUND);
        CardResponseDto targetCard = findCard(targetNumber, ErrorCode.CARD_RECIPIENT_NOT_FOUND);
        if (!Objects.equals(sourceCard.clientId(), clientId)) {
            throw new BusinessException(FORBIDDEN, ErrorCode.CARD_SENDER_NOT_OWNED);
        }
        validateActiveCard(sourceCard, ErrorCode.CARD_SENDER_UNAVAILABLE);
        validateActiveCard(targetCard, ErrorCode.CARD_RECIPIENT_UNAVAILABLE);
        if (Objects.equals(sourceCard.accountId(), targetCard.accountId())) {
            throw new BusinessException(BAD_REQUEST, ErrorCode.OPERATION_SAME_ACCOUNT);
        }

        Account source = findAccount(sourceCard.accountId());
        Account target = findAccount(targetCard.accountId());
        validateCardAccount(source, sourceCard);
        validateCardAccount(target, targetCard);

        Client sender = clientRepository.findById(clientId)
                .orElseThrow(() -> new BusinessException(
                        NOT_FOUND,
                        ErrorCode.CLIENT_NOT_FOUND,
                        clientId
                ));
        Client recipient = clientRepository.findById(targetCard.clientId())
                .orElseThrow(() -> new BusinessException(
                        NOT_FOUND,
                        ErrorCode.OPERATION_RECIPIENT_NOT_FOUND,
                        targetCard.clientId()
                ));
        validateRecipient(recipient);
        String recipientName = fullName(recipient);
        if (recipientName.isBlank()) {
            throw new BusinessException(CONFLICT, ErrorCode.OPERATION_RECIPIENT_NAME_MISSING);
        }

        return new CardOperationContextResponse(
                clientId,
                targetCard.clientId(),
                sourceNumber,
                targetNumber,
                source.getBalance(),
                source.getCurrency(),
                target.getCurrency(),
                currencyService.getRateFromCache(source.getCurrency()),
                currencyService.getRateFromCache(target.getCurrency()),
                fullName(sender),
                recipientName
        );
    }

    private CardResponseDto resolvePhoneRecipientCard(String phone, String sourceNumber, UUID clientId) {
        CardResponseDto sourceCard = findCard(sourceNumber, ErrorCode.CARD_SENDER_NOT_FOUND);
        if (!Objects.equals(sourceCard.clientId(), clientId)) {
            throw new BusinessException(FORBIDDEN, ErrorCode.CARD_SENDER_NOT_OWNED);
        }
        validateActiveCard(sourceCard, ErrorCode.CARD_SENDER_UNAVAILABLE);
        if (sourceCard.currency() != RUB) {
            throw new BusinessException(BAD_REQUEST, ErrorCode.OPERATION_PHONE_RUB_ONLY);
        }

        Client recipient = clientRepository.findByPhone(phone)
                .orElseThrow(() -> new BusinessException(
                        NOT_FOUND,
                        ErrorCode.OPERATION_RECIPIENT_PHONE_NOT_FOUND,
                        phone
                ));
        validateRecipient(recipient);

        List<CardResponseDto> cards = cardRepository.findCardsWithBalanceByClientId(recipient.getId());
        return cards.stream()
                .filter(card -> card.status() == Status.ACTIVE)
                .filter(card -> card.currency() == RUB)
                .filter(card -> !Objects.equals(card.accountId(), sourceCard.accountId()))
                .min(Comparator.comparing(CardResponseDto::cardNumber))
                .orElseThrow(() -> new BusinessException(
                        CONFLICT,
                        ErrorCode.OPERATION_RECIPIENT_RUB_CARD_UNAVAILABLE
                ));
    }

    private CardResponseDto findCard(String number, ErrorCode errorCode) {
        return cardRepository.findByNumber(number)
                .orElseThrow(() -> new BusinessException(NOT_FOUND, errorCode, number));
    }

    private Account findAccount(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(NOT_FOUND, ErrorCode.ACCOUNT_NOT_FOUND, id));
    }

    private Account findClientAccount(UUID id, UUID clientId) {
        return accountRepository.findByIdAndClientId(id, clientId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND, ErrorCode.ACCOUNT_NOT_FOUND, id));
    }

    private Account findAccountForUpdate(UUID id) {
        return accountRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(NOT_FOUND, ErrorCode.ACCOUNT_NOT_FOUND, id));
    }

    private void validateActiveCard(CardResponseDto card, ErrorCode errorCode) {
        if (card.status() != Status.ACTIVE) {
            throw new BusinessException(CONFLICT, errorCode);
        }
    }

    private void validateCardAccount(Account account, CardResponseDto card) {
        validateActiveAccount(account);
        if (!Objects.equals(account.getClientId(), card.clientId())
                || account.getCurrency() != card.currency()) {
            throw new BusinessException(CONFLICT, ErrorCode.CARD_ACCOUNT_MISMATCH);
        }
    }

    private void validateOwnedAccount(Account account, UUID clientId) {
        if (!Objects.equals(account.getClientId(), clientId)) {
            throw new BusinessException(FORBIDDEN, ErrorCode.ACCOUNT_NOT_OWNED);
        }
    }

    private void validateActiveAccount(Account account) {
        if (account.getStatus() != Status.ACTIVE) {
            throw new BusinessException(CONFLICT, ErrorCode.ACCOUNT_CLOSED);
        }
    }

    private void validateRecipient(Client recipient) {
        if (recipient.getStatus() == Status.BLOCKED) {
            throw new BusinessException(CONFLICT, ErrorCode.OPERATION_RECIPIENT_BLOCKED);
        }
        if (recipient.getStatus() != Status.ACTIVE) {
            throw new BusinessException(CONFLICT, ErrorCode.OPERATION_RECIPIENT_UNAVAILABLE);
        }
    }

    private String fullName(Client client) {
        return (
                Objects.toString(client.getFirstName(), "")
                        + " "
                        + Objects.toString(client.getLastName(), "")
        ).trim();
    }

    public void verifyInternalToken(String token) {
        if (!internalToken.equals(token)) {
            throw new BusinessException(FORBIDDEN, ErrorCode.INTERNAL_ACCESS_DENIED);
        }
    }
}
