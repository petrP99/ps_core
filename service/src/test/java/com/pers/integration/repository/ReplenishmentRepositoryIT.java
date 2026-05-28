package com.pers.integration.repository;

import com.pers.dto.filter.ReplenishmentFilterDto;
import com.pers.entity.Card;
import com.pers.entity.Client;
import com.pers.entity.Replenishment;
import com.pers.enums.Role;
import static com.pers.enums.Status.ACTIVE;
import static com.pers.enums.Status.FAILED;
import static com.pers.enums.Status.IN_PROGRESS;
import static com.pers.enums.Status.SUCCESS;
import com.pers.entity.User;
import com.pers.integration.BaseIntegrationIT;
import com.pers.repository.CardRepository;
import com.pers.repository.ClientRepository;
import com.pers.repository.ReplenishmentRepository;
import com.pers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
class ReplenishmentRepositoryIT extends BaseIntegrationIT {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final ReplenishmentRepository replenishmentRepository;
    private Replenishment replenishment;
    private Replenishment replenishment2;

    @BeforeEach
    void init() {
        var user = User.builder()
                .login("test@mail.ru")
                .password("123")
                .role(Role.USER)
                .build();
        var client = Client.builder()
                .user(user)
                .firstName("Ivan")
                .lastName("Ivanov")
                .phone("89638527412")
                .createdTime(LocalDateTime.now())
                .balance(new BigDecimal(0))
                .status(ACTIVE)
                .build();
        var card = Card.builder()
                .client(client)
                .balance(new BigDecimal(0))
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(5))
                .status(ACTIVE)
                .build();
        replenishment = Replenishment.builder()
                .clientTo(client)
                .cardNoTo(card)
                .amount(new BigDecimal(777))
                .timeOfReplenishment(LocalDateTime.now())
                .status(SUCCESS)
                .build();
        replenishment2 = Replenishment.builder()
                .clientTo(client)
                .cardNoTo(card)
                .amount(new BigDecimal(333))
                .timeOfReplenishment(LocalDateTime.now())
                .status(FAILED)
                .build();

        userRepository.save(user);
        clientRepository.save(client);
        cardRepository.save(card);
        replenishmentRepository.save(replenishment);
        replenishmentRepository.save(replenishment2);
    }

    @Test
    void create() {
        var result = replenishmentRepository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(replenishment);
    }

    @Test
    void update() {
        replenishment.setStatus(IN_PROGRESS);
        replenishmentRepository.save(replenishment);

        assertThat(replenishment.getStatus()).isEqualTo(IN_PROGRESS);
    }

    @Test
    void delete() {
        replenishmentRepository.delete(replenishment2);

        var result = replenishmentRepository.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findByFilter() {
        var filter = ReplenishmentFilterDto.builder()
                .amount(BigDecimal.valueOf(333))
                .build();
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        var result = replenishmentRepository.findAllByFilter(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findAllByClientToId() {
        var result = replenishmentRepository.findAllByClientToId(replenishment.getClientTo().getId());

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
    }
}