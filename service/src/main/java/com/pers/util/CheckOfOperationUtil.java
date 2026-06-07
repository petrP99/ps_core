package com.pers.util;

import com.pers.entity.Account;
import com.pers.entity.Card;
import com.pers.repository.AccountRepository;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class CheckOfOperationUtil {

    public static BigDecimal calculateClientBalance(List<Card> cards, AccountRepository accountRepository) {
        return cards.stream()
                .map(Card::getAccountId)
                .filter(Objects::nonNull)
                .distinct()
                .map(id -> accountRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}