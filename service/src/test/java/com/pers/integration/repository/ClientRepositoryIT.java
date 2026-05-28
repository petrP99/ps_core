package com.pers.integration.repository;

import com.pers.dto.filter.ClientFilterDto;
import com.pers.entity.Client;
import com.pers.enums.Role;
import static com.pers.enums.Status.ACTIVE;
import static com.pers.enums.Status.BLOCKED;
import com.pers.entity.User;
import com.pers.integration.BaseIntegrationIT;
import com.pers.repository.ClientRepository;
import com.pers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@RequiredArgsConstructor
class ClientRepositoryIT extends BaseIntegrationIT {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private User user;
    private User user2;
    private Client client;
    private Client client2;

    @BeforeEach
    void init() {
        user = User.builder()
                .login("test@mail.ru")
                .password("123")
                .role(Role.USER)
                .build();
        user2 = User.builder()
                .login("test2@mail.ru")
                .password("123")
                .role(Role.USER)
                .build();
        client = Client.builder()
                .user(user)
                .firstName("Ivan")
                .lastName("Ivanov")
                .phone("89638527412")
                .createdTime(LocalDateTime.now())
                .balance(new BigDecimal(0))
                .status(ACTIVE)
                .build();
        client2 = Client.builder()
                .user(user2)
                .firstName("Petr")
                .lastName("Petrov")
                .phone("89632589632")
                .createdTime(LocalDateTime.now())
                .balance(new BigDecimal(0))
                .status(ACTIVE)
                .build();
        userRepository.save(user);
        userRepository.save(user2);
        clientRepository.save(client);
        clientRepository.save(client2);
    }


    @Test
    void create() {
        var result = clientRepository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(1)).isEqualTo(client2);
    }

    @Test
    void update() {
        client2.setStatus(BLOCKED);
        clientRepository.save(client2);

        assertThat(client2.getStatus()).isEqualTo(BLOCKED);
    }

    @Test
    void delete() {
        clientRepository.delete(client);

        var result = clientRepository.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findByFilter() {
        var filter = ClientFilterDto.builder()
                .firstName("petr")
                .build();
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        var result = clientRepository.findAllByFilter(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findByUserLogin() {
        var result = clientRepository.findByUserLogin("test@mail.ru");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(client);
    }
}