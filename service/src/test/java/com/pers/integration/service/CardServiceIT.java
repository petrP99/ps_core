package com.pers.integration.service;

import com.pers.dto.CardCreateDto;
import com.pers.entity.Card;
import com.pers.entity.Client;
import com.pers.enums.Currency;
import com.pers.enums.Role;
import com.pers.enums.Status;
import com.pers.entity.User;
import com.pers.integration.BaseIntegrationIT;
import com.pers.service.CardService;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
class CardServiceIT extends BaseIntegrationIT {

    private final CardService cardService;
    private User user;
    private Client client;
    private Card card;
    private CardCreateDto cardCreateDto;

    @BeforeEach
    void prepare() {
        user = User.builder()
                .login("user10@mail.ru")
                .password("123")
                .role(Role.USER)
                .build();

        client = Client.builder()
                .user(user)
                .balance(new BigDecimal(0))
                .firstName("Petr")
                .lastName("Petrov")
                .phone("89632587854")
                .createdTime(LocalDateTime.now())
                .status(Status.ACTIVE)
                .build();

        card = Card.builder()
                .client(client)
                .balance(new BigDecimal(100))
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(5))
                .name("тестовая")
                .currency(Currency.RUB)
                .status(Status.ACTIVE)
                .build();

        var card2 = Card.builder()
                .client(client)
                .balance(new BigDecimal(50))
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(5))
                .name("для покупок")
                .currency(Currency.RUB)
                .status(Status.ACTIVE)
                .build();

        entityManager.persist(user);
        entityManager.persist(client);
        entityManager.persist(card);
        entityManager.persist(card2);

        cardCreateDto = new CardCreateDto(card.getClient().getId(), card.getBalance(), card.getCreatedDate(), card.getExpireDate(), card.getName(), card.getCurrency(), card.getStatus());
    }

    @Test
    void findByClientId() {
        var result = cardService.findByClientId(client.getId());

        assertThat(result).isNotEmpty();
    }

    @Test
    void findById() {
        var result = cardService.findById(card.getId());

        assertThat(result).isPresent();
    }

    @Test
    void create() {
        var result = cardService.create(cardCreateDto);

        assertThat(result.status()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void updateStatus() {
        var cardReadDto = cardService.findById(card.getId()).orElseThrow();

        var result = cardService.updateStatusToBlocked(cardReadDto);

        assertThat(result).isPresent();
        result.ifPresent(card1 ->
                assertAll(() -> {
                    assertThat(card1.status()).isEqualTo(Status.BLOCKED);
                }));
    }

    @Test
    void findActiveCardsAndPositiveBalanceByClientId() {
        var result = cardService.findActiveCardsAndPositiveBalanceByClientId(client.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void findCardByClientPhone() {
        var result = cardService.findCardByClientPhone("89632587854");

        assertThat(result).isPresent();
    }

}
