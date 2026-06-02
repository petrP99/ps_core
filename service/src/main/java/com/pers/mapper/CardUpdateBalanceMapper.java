package com.pers.mapper;

import com.pers.dto.CardUpdateBalanceDto;
import com.pers.entity.Card;
import com.pers.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardUpdateBalanceMapper implements Mapper<CardUpdateBalanceDto, Card> {

    private final ClientRepository clientRepository;

    @Override
    public Card mapFrom(CardUpdateBalanceDto object) {
        return Card.builder()
                .id(object.id())
                .client(clientRepository.findById(object.clientId()).orElseThrow(IllegalArgumentException::new))
                .balance(object.balance())
                .createdDate(object.createdDate())
                .expireDate(object.expireDate())
                .name(object.name())
                .currency(object.currency())
                .status(object.status())
                .build();
    }

}
