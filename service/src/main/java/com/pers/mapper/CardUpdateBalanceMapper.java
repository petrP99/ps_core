package com.pers.mapper;

import com.pers.dto.CardUpdateBalanceDto;
import com.pers.entity.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardUpdateBalanceMapper implements Mapper<CardUpdateBalanceDto, Card> {

    @Override
    public Card mapFrom(CardUpdateBalanceDto object) {
        return Card.builder()
                .id(object.id())
                .clientId(object.clientId())
                .accountId(object.accountId())
                .createdDate(object.createdDate())
                .expireDate(object.expireDate())
                .name(object.name())
                .currency(object.currency())
                .status(object.status())
                .build();
    }

}