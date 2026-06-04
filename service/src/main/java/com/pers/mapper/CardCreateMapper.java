package com.pers.mapper;

import com.pers.dto.CardCreateDto;
import com.pers.dto.CardCreateDto2;
import com.pers.dto.CardReadDto;
import com.pers.entity.Card;
import com.pers.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CardCreateMapper implements Mapper<CardCreateDto, Card>, MapperStatus<CardReadDto, Card> {

    public static final long YEARS_TO_EXPIRED = 5L;

    @Override
    public Card mapFrom(CardCreateDto object) {
        return Card.builder()
                .clientId(object.clientId())
                .accountId(object.accountId())
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(5L))
                .name(object.name())
                .currency(object.currency())
                .status(Status.ACTIVE)
                .build();
    }

    public Card mapFrom(CardCreateDto2 object, UUID clientId) {
        return Card.builder()
                .clientId(clientId)
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(YEARS_TO_EXPIRED))
                .name(object.name())
                .currency(object.currency())
                .status(Status.ACTIVE)
                .build();
    }

    @Override
    public Card mapStatusToBlocked(CardReadDto object) {
        return Card.builder()
                .id(object.id())
                .clientId(object.clientId())
                .accountId(object.accountId())
                .createdDate(object.createdDate())
                .expireDate(object.expireDate())
                .name(object.name())
                .currency(object.currency())
                .status(Status.BLOCKED)
                .build();
    }

    public Card mapStatusExpired(CardReadDto object) {
        return Card.builder()
                .id(object.id())
                .clientId(object.clientId())
                .accountId(object.accountId())
                .createdDate(object.createdDate())
                .expireDate(object.expireDate())
                .name(object.name())
                .currency(object.currency())
                .status(Status.EXPIRED)
                .build();
    }
}