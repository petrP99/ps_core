package com.pers.service;

import com.pers.dto.AccountCreateDto;
import com.pers.dto.AccountReadDto;
import com.pers.entity.Account;
import com.pers.mapper.AccountCreateMapper;
import com.pers.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountCreateMapper accountCreateMapper;
    private final CardService cardService;
    private final CurrencyService currencyService;

    @Transactional
    public AccountReadDto create(AccountCreateDto dto, UUID clientId) {
        var account = accountCreateMapper.toEntity(dto, clientId);
        var savedAccount = accountRepository.save(account);
        return toDto(savedAccount);
    }

    public Optional<AccountReadDto> findById(UUID id) {
        return accountRepository.findById(id)
                .map(this::toDto);
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

    private AccountReadDto toDto(Account account) {
        var cards = cardService.findByAccountId(account.getId());
        return new AccountReadDto(
                account.getId(),
                account.getBalance(),
                account.getCurrency(),
                account.getName(),
                account.getCashback(),
                cards
        );
    }
}
