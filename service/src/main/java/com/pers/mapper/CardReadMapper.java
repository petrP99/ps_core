package com.pers.mapper;

import com.pers.dto.CardReadDto;
import com.pers.entity.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardReadMapper implements Mapper<Card, CardReadDto> {

    @Override
    public CardReadDto mapFrom(Card object) {
        return new CardReadDto(
                object.getId(),
                object.getClient().getId(),
                object.getBalance(),
                object.getCreatedDate(),
                object.getExpireDate(),
                object.getName(),
                object.getCurrency(),
                object.getStatus()
        );
    }
}
