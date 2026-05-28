package com.pers.integration.repository;

import com.pers.dto.filter.CardFilterDto;
import com.pers.entity.Card;
import com.pers.entity.Client;
import com.pers.enums.Role;
import static com.pers.enums.Status.ACTIVE;
import static com.pers.enums.Status.BLOCKED;
import com.pers.entity.User;
import com.pers.integration.BaseIntegrationIT;
import com.pers.repository.CardRepository;
import com.pers.repository.ClientRepository;
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
class CardRepositoryIT extends BaseIntegrationIT {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    private Card card;
    private Card card2;

    @BeforeEach
    void init() {
        var user = User.builder()
                .login("test@mail.ru")
                .password("123")
                .role(Role.USER)
                .build();
        var user2 = User.builder()
                .login("test2@mail.ru")
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
        var client2 = Client.builder()
                .user(user2)
                .firstName("Petr")
                .lastName("Petrov")
                .phone("89632589632")
                .createdTime(LocalDateTime.now())
                .balance(new BigDecimal(0))
                .status(ACTIVE)
                .build();
        card = Card.builder()
                .client(client)
                .balance(new BigDecimal(0))
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(5))
                .status(ACTIVE)
                .build();
        card2 = Card.builder()
                .client(client2)
                .balance(new BigDecimal(0))
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(5))
                .status(BLOCKED)
                .build();
        userRepository.save(user);
        userRepository.save(user2);
        clientRepository.save(client);
        clientRepository.save(client2);
        cardRepository.save(card);
        cardRepository.save(card2);
    }


    @Test
    void create() {
        var result = cardRepository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(1)).isEqualTo(card2);
    }

    @Test
    void update() {
        card2.setBalance(new BigDecimal(100));
        cardRepository.save(card2);

        assertThat(card2.getBalance()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void delete() {
        cardRepository.delete(card);
        cardRepository.delete(card2);

        var result = cardRepository.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findByFilter() {
        var filter = CardFilterDto.builder()
                .status(ACTIVE)
                .build();
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        var result = cardRepository.findAllByFilter(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findByClientPhone() {
        var result = cardRepository.findByClientPhone("89632589632");

        assertThat(result).isNotEmpty();
    }
}