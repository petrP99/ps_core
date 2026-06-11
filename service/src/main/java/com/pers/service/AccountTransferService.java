package com.pers.service;

import com.pers.dto.request.AccountTransferRequestDto;
import com.pers.dto.response.AccountTransferResponseDto;
import com.pers.entity.Account;
import com.pers.entity.AccountTransfer;
import com.pers.enums.Status;
import com.pers.exception.AccountException;
import com.pers.exception.ErrorCode;
import com.pers.repository.AccountRepository;
import com.pers.repository.AccountTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AccountTransferService {

    private final AccountRepository accountRepository;
    private final AccountTransferRepository accountTransferRepository;
    private final CurrencyService currencyService;

    @Transactional(readOnly = true)
    public AccountTransferResponseDto preview(AccountTransferRequestDto request, UUID clientId) {
        Account accountFrom = findClientAccount(request.accountFrom(), clientId);
        Account accountTo = findClientAccount(request.accountTo(), clientId);
        return validateAndCalculate(request, clientId, accountFrom, accountTo);
    }

    @Transactional
    public AccountTransferResponseDto transfer(AccountTransferRequestDto request, UUID clientId) {
        if (Objects.equals(request.accountFrom(), request.accountTo())) {
            throw new AccountException(BAD_REQUEST, ErrorCode.ACCOUNT_SAME);
        }

        UUID firstId = request.accountFrom().compareTo(request.accountTo()) < 0
                ? request.accountFrom()
                : request.accountTo();
        UUID secondId = Objects.equals(firstId, request.accountFrom())
                ? request.accountTo()
                : request.accountFrom();

        Account firstAccount = findAccountForUpdate(firstId);
        Account secondAccount = findAccountForUpdate(secondId);
        Account accountFrom = Objects.equals(firstAccount.getId(), request.accountFrom())
                ? firstAccount
                : secondAccount;
        Account accountTo = Objects.equals(firstAccount.getId(), request.accountTo())
                ? firstAccount
                : secondAccount;

        AccountTransferResponseDto result = validateAndCalculate(
                request,
                clientId,
                accountFrom,
                accountTo
        );

        accountFrom.setBalance(accountFrom.getBalance().subtract(result.amount()));
        accountTo.setBalance(accountTo.getBalance().add(result.amountTo()));
        AccountTransfer savedTransfer = accountTransferRepository.save(
                AccountTransfer.builder()
                        .clientId(clientId)
                        .accountFrom(result.accountFrom())
                        .accountFromName(result.accountFromName())
                        .accountTo(result.accountTo())
                        .accountToName(result.accountToName())
                        .amount(result.amount())
                        .amountTo(result.amountTo())
                        .exchangeRate(result.exchangeRate())
                        .currency(result.currency())
                        .targetCurrency(result.targetCurrency())
                        .timeOfTransfer(LocalDateTime.now())
                        .build()
        );
        return toResponse(savedTransfer);
    }

    private AccountTransferResponseDto validateAndCalculate(
            AccountTransferRequestDto request,
            UUID clientId,
            Account accountFrom,
            Account accountTo
    ) {
        if (Objects.equals(accountFrom.getId(), accountTo.getId())) {
            throw new AccountException(BAD_REQUEST, ErrorCode.ACCOUNT_SAME);
        }
        validateAccount(accountFrom, clientId);
        validateAccount(accountTo, clientId);

        BigDecimal amount = request.amount().setScale(2, RoundingMode.HALF_UP);
        if (accountFrom.getBalance().compareTo(amount) < 0) {
            throw new AccountException(CONFLICT, ErrorCode.ACCOUNT_INSUFFICIENT_FUNDS);
        }

        CurrencyService.ConversionResult conversion = currencyService.convert(
                amount,
                accountFrom.getCurrency(),
                accountTo.getCurrency()
        );

        return new AccountTransferResponseDto(
                null,
                accountFrom.getId(),
                accountFrom.getName(),
                accountTo.getId(),
                accountTo.getName(),
                amount,
                conversion.convertedAmount(),
                conversion.exchangeRate(),
                accountFrom.getCurrency(),
                accountTo.getCurrency(),
                null
        );
    }

    private AccountTransferResponseDto toResponse(AccountTransfer transfer) {
        return new AccountTransferResponseDto(
                transfer.getId(),
                transfer.getAccountFrom(),
                transfer.getAccountFromName(),
                transfer.getAccountTo(),
                transfer.getAccountToName(),
                transfer.getAmount(),
                transfer.getAmountTo(),
                transfer.getExchangeRate(),
                transfer.getCurrency(),
                transfer.getTargetCurrency(),
                transfer.getTimeOfTransfer()
        );
    }

    private Account findClientAccount(UUID accountId, UUID clientId) {
        return accountRepository.findByIdAndClientId(accountId, clientId)
                .orElseThrow(() -> new AccountException(
                        NOT_FOUND,
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        accountId
                ));
    }

    private Account findAccountForUpdate(UUID accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountException(
                        NOT_FOUND,
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        accountId
                ));
    }

    private void validateAccount(Account account, UUID clientId) {
        if (!Objects.equals(account.getClientId(), clientId)) {
            throw new AccountException(FORBIDDEN, ErrorCode.ACCOUNT_NOT_OWNED);
        }
        if (account.getStatus() != Status.ACTIVE) {
            throw new AccountException(CONFLICT, ErrorCode.ACCOUNT_CLOSED);
        }
    }
}
