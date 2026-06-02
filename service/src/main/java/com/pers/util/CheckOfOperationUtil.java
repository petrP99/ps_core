package com.pers.util;

import com.pers.dto.CardReadDto;
import com.pers.dto.CardUpdateBalanceDto;
import com.pers.dto.ClientReadDto;
import com.pers.dto.ClientUpdateBalanceDto;
import com.pers.entity.Card;
import com.pers.enums.Operation;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class CheckOfOperationUtil {

    public static BigDecimal calculateClientBalance(List<Card> cards) {
        return cards.stream()
                .map(Card::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static ClientUpdateBalanceDto createClientUpdateBalanceDto(ClientReadDto clientReadDto, BigDecimal newBalance) {
        return new ClientUpdateBalanceDto(
                clientReadDto.getId(),
                newBalance,
                clientReadDto.getFirstName(),
                clientReadDto.getLastName(),
                clientReadDto.getPhone(),
                clientReadDto.getStatus(),
                clientReadDto.getCreatedTime());
    }

    public static CardUpdateBalanceDto getCardUpdateBalanceDto(CardReadDto cardReadDto, BigDecimal amount, Operation operation) {
        return new CardUpdateBalanceDto(
                cardReadDto.id(),
                cardReadDto.clientId(),
                operation == Operation.ADD ? cardReadDto.balance().add(amount) : cardReadDto.balance().subtract(amount),
                cardReadDto.createdDate(),
                cardReadDto.expireDate(),
                cardReadDto.name(),
                cardReadDto.currency(),
                cardReadDto.status());
    }
}