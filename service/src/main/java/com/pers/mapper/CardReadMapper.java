package com.pers.mapper;

import com.pers.dto.CardReadDto;
import com.pers.entity.Account;
import com.pers.entity.Card;
import com.pers.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CardReadMapper implements Mapper<Card, CardReadDto> {

    private final AccountRepository accountRepository;

    @Override
    public CardReadDto mapFrom(Card object) {
        BigDecimal balance = accountRepository.findById(object.getAccountId())
                .map(Account::getBalance)
                .orElse(BigDecimal.ZERO);
        return new CardReadDto(
                object.getId(),
                object.getClientId(),
                object.getAccountId(),
                balance,
                object.getCreatedDate(),
                object.getExpireDate(),
                object.getName(),
                object.getCurrency(),
                object.getStatus()
        );
    }
}