package com.pers.mapper;

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
public class CardMapper {

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
}