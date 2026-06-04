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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountCreateMapper accountCreateMapper;
    private final CardService cardService;

    @Transactional
    public AccountReadDto create(AccountCreateDto dto, UUID clientId) {
        var account = accountCreateMapper.mapFrom(dto, clientId);
        var savedAccount = accountRepository.save(account);
        return toReadDto(savedAccount);
    }

    public Optional<AccountReadDto> findById(UUID id) {
        return accountRepository.findById(id)
                .map(this::toReadDto);
    }

    public BigDecimal getClientTotalBalance(UUID clientId) {
        return accountRepository.getTotalBalanceByClientId(clientId);
    }

    private AccountReadDto toReadDto(Account account) {
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
