package com.pers.mapper;

import com.pers.dto.CardCreateDto;
import com.pers.dto.CardCreateDto2;
import com.pers.dto.CardReadDto;
import com.pers.entity.Card;
import com.pers.enums.Status;
import com.pers.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.math.BigDecimal.ZERO;

@Component
@RequiredArgsConstructor
public class CardCreateMapper implements Mapper<CardCreateDto, Card>, MapperStatus<CardReadDto, Card> {

    public static final long YEARS_TO_EXPIRED = 5L;
    private final ClientRepository clientRepository;

    @Override
    public Card mapFrom(CardCreateDto object) {
        return Card.builder()
                .client(clientRepository.findById(object.clientId()).orElseThrow(IllegalArgumentException::new))
                .balance(new BigDecimal(0))
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(5L))
                .name(object.name())
                .currency(object.currency())
                .status(Status.ACTIVE)
                .build();
    }

    public Card mapFrom(CardCreateDto2 object, Long clientId) {
        return Card.builder()
                .client(clientRepository.findById(clientId).orElseThrow(IllegalArgumentException::new)) //todo зачем передавать всего клиента
                .balance(ZERO)
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
                .client(clientRepository.findById(object.clientId()).orElseThrow(IllegalArgumentException::new))
                .balance(object.balance())
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
                .client(clientRepository.findById(object.clientId()).orElseThrow(IllegalArgumentException::new))
                .balance(object.balance())
                .createdDate(object.createdDate())
                .expireDate(object.expireDate())
                .name(object.name())
                .currency(object.currency())
                .status(Status.EXPIRED)
                .build();
    }
}
