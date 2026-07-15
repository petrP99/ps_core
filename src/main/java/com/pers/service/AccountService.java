package com.pers.service;

import com.pers.dto.request.AccountRequestDto;
import com.pers.dto.response.AccountResponseDto;
import com.pers.entity.Account;
import com.pers.mapper.AccountCreateMapper;
import com.pers.mapper.AccountReadMapper;
import com.pers.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountCreateMapper accountCreateMapper;
    private final AccountReadMapper accountReadMapper;
    private final CardService cardService;
    private final CurrencyService currencyService;
    private final NotificationPublisherService notificationPublisherService;

    @Transactional
    public AccountResponseDto create(AccountRequestDto dto, UUID clientId) {
        var account = accountCreateMapper.toEntity(dto, clientId);
        var savedAccount = accountRepository.save(account);
        var response = toDto(savedAccount);
        notificationPublisherService.publish(
                clientId,
                "ACCOUNT_CREATED",
                "Счет открыт",
                "Открыт счет " + response.name() + " в валюте " + response.currency(),
                "ps_core",
                response.id().toString()
        );
        return response;
    }

    public Optional<AccountResponseDto> findById(UUID id) {
        return accountRepository.findById(id)
                .map(this::toDto);
    }

    public List<AccountResponseDto> findAll(UUID clientId) {
        return accountRepository.findAllByClientId(clientId).stream()
                .map(this::toDto)
                .toList();
    }
    
    public BigDecimal getClientTotalBalance(UUID clientId) {
        return accountRepository.findAllByClientId(clientId).stream()
                .collect(Collectors.toMap(
                        Account::getCurrency,
                        Account::getBalance,
                        BigDecimal::add
                ))
                .entrySet().stream()
                .map(entry -> {
                    BigDecimal rateExchange = currencyService.getRateFromCache(entry.getKey());
                    return entry.getValue().multiply(rateExchange);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private AccountResponseDto toDto(Account account) {
        var cards = cardService.findByAccountId(account.getId());
        return accountReadMapper.toDto(account, cards);
    }
}
