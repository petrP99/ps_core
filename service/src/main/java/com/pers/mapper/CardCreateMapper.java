package com.pers.mapper;

import com.pers.dto.response.CardResponseDto;
import com.pers.dto.request.CardRequestDto;
import com.pers.entity.Card;
import com.pers.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

import static com.pers.util.constant.Constants.YEARS_TO_EXPIRED;

@Component
@RequiredArgsConstructor
public class CardCreateMapper implements Mapper<CardRequestDto, Card>, MapperStatus<CardResponseDto, Card> {


    @Override
    public Card toEntity(CardRequestDto object) {
        return Card.builder()
//                .clientId(object.clientId())
                .accountId(object.accountId())
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(5L))
                .name(object.name())
                .currency(object.currency())
                .status(Status.ACTIVE)
//                .cardNumber(object.cardNumber())
                .build();
    }

    public Card toEntity(CardRequestDto cardDto, UUID clientId, String cardNumber) {
        return Card.builder()
                .clientId(clientId)
                .accountId(cardDto.accountId())
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(YEARS_TO_EXPIRED))
                .name(cardDto.name())
                .currency(cardDto.currency())
                .status(Status.ACTIVE)
                .cardNumber(cardNumber)
                .build();
    }

    @Override
    public Card mapStatusToBlocked(CardResponseDto object) {
        return Card.builder()
                .id(object.id())
                .clientId(object.clientId())
                .accountId(object.accountId())
                .createdDate(object.createdDate())
                .expireDate(object.expireDate())
                .name(object.name())
                .currency(object.currency())
                .status(Status.BLOCKED)
                .cardNumber(object.cardNumber())
                .build();
    }

    public Card mapStatusExpired(CardResponseDto object) {
        return Card.builder()
                .id(object.id())
                .clientId(object.clientId())
                .accountId(object.accountId())
                .createdDate(object.createdDate())
                .expireDate(object.expireDate())
                .name(object.name())
                .currency(object.currency())
                .status(Status.EXPIRED)
                .cardNumber(object.cardNumber())
                .build();
    }
}