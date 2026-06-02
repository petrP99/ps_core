package com.pers.integration.mapper;

import com.pers.dto.CardCreateDto;
import com.pers.dto.CardReadDto;
import com.pers.entity.Card;
import com.pers.entity.Client;
import com.pers.enums.Currency;
import com.pers.enums.Role;
import com.pers.enums.Status;
import com.pers.entity.User;
import com.pers.integration.BaseIntegrationIT;
import com.pers.mapper.CardCreateMapper;
import com.pers.mapper.CardReadMapper;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
class CardCreateMapperIT extends BaseIntegrationIT {

    private final CardCreateMapper cardCreateMapper;
    private final CardReadMapper cardReadMapper;
    private User user;
    private Client client;
    private Card card;
    private CardReadDto cardReadDto;
    private CardCreateDto cardCreateDto;

    @BeforeEach
    void prepare() {
        user = User.builder()
                .login("test@mail.ru")
                .password("123")
                .role(Role.USER)
                .build();
        entityManager.persist(user);

        client = Client.builder()
                .user(user)
                .balance(new BigDecimal(0))
                .firstName("petr")
                .lastName("petrov")
                .phone("89638521478")
                .createdTime(LocalDateTime.now())
                .status(Status.ACTIVE)
                .build();
        entityManager.persist(client);

        card = Card.builder()
                .client(client)
                .balance(new BigDecimal(100))
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(3))
                .name("тестовая")
                .currency(Currency.RUB)
                .status(Status.ACTIVE)
                .build();
        entityManager.persist(card);

        cardReadDto = cardReadMapper.mapFrom(card);
        cardCreateDto = new CardCreateDto(card.getClient().getId(), card.getBalance(), card.getCreatedDate(), card.getExpireDate(), card.getName(), card.getCurrency(), Status.BLOCKED);
    }

    @Test
    void mapFrom() {
        var result = cardCreateMapper.mapFrom(cardCreateDto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus().name()).isEqualTo("ACTIVE");
    }

    @Test
    void mapStatusToBlocked() {
        var result = cardCreateMapper.mapStatusToBlocked(cardReadDto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus().name()).isEqualTo("BLOCKED");
    }

    @Test
    void mapStatusExpired() {
        var result = cardCreateMapper.mapStatusExpired(cardReadDto);


        assertThat(result).isNotNull();
        assertThat(result.getStatus().name()).isEqualTo("EXPIRED");
    }

}


















