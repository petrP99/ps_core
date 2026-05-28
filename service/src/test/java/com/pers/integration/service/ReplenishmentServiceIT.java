package com.pers.integration.service;

import com.pers.dto.ReplenishmentCreateDto;
import com.pers.dto.filter.ReplenishmentFilterDto;
import com.pers.entity.Card;
import com.pers.entity.Client;
import com.pers.entity.Replenishment;
import com.pers.enums.Role;
import com.pers.enums.Status;
import com.pers.entity.User;
import com.pers.integration.BaseIntegrationIT;
import com.pers.service.ReplenishmentService;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
class ReplenishmentServiceIT extends BaseIntegrationIT {

    private final ReplenishmentService replenishmentService;
    private User user;
    private Client client;
    private Card card;
    private Replenishment replenishment;
    private ReplenishmentCreateDto replenishmentCreateDto;

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
                .balance(new BigDecimal(1000))
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(5))
                .status(Status.ACTIVE)
                .build();

        var card2 = Card.builder()
                .client(client)
                .balance(new BigDecimal(0))
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(5))
                .status(Status.ACTIVE)
                .build();

        replenishment = Replenishment.builder()
                .clientTo(client)
                .cardNoTo(card)
                .amount(new BigDecimal(250))
                .timeOfReplenishment(LocalDateTime.now())
                .status(Status.SUCCESS)
                .build();

        var replenishment2 = Replenishment.builder()
                .clientTo(client)
                .cardNoTo(card2)
                .amount(new BigDecimal(200))
                .timeOfReplenishment(LocalDateTime.now())
                .status(Status.SUCCESS)
                .build();

        entityManager.persist(user);
        entityManager.persist(client);
        entityManager.persist(card);
        entityManager.persist(card2);
        entityManager.persist(replenishment);
        entityManager.persist(replenishment2);

        replenishmentCreateDto = new ReplenishmentCreateDto(
                replenishment.getClientTo().getId(),
                replenishment.getCardNoTo().getId(),
                replenishment.getAmount(),
                replenishment.getTimeOfReplenishment(),
                replenishment.getStatus());
    }

    @Test
    void checkAndCreateReplenishment() {
        var result = replenishmentService.checkAndCreateReplenishment(replenishmentCreateDto);

        assertTrue(result);
    }

    @Test
    void findById() {
        var result = replenishmentService.findById(replenishment.getId());

        assertThat(result).isPresent();
    }

    @Test
    void create() {
        var result = replenishmentService.create(replenishmentCreateDto);

        assertThat(result.cardNo()).isEqualTo(card.getId());
    }

    @Test
    void findByClientByFilter() {
        var filter = new ReplenishmentFilterDto(null, null, null, null, null, null);
        var result = replenishmentService.findByClientByFilter(filter, Pageable.ofSize(1), replenishment.getClientTo().getId());

        assertThat(result).hasSize(1);
    }

}
