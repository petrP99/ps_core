package com.pers.mapper;

import com.pers.dto.response.CardResponseDto;
import com.pers.entity.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CardReadMapper {

    public CardResponseDto toDto(Card card, BigDecimal balance) {

        return new CardResponseDto(
                card.getId(),
                card.getClientId(),
                card.getAccountId(),
                balance,
                card.getCreatedDate(),
                card.getExpireDate(),
                card.getName(),
                card.getCurrency(),
                card.getStatus(),
                card.getCardNumber()
        );
    }
}