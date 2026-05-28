package com.pers.integration.service;

import com.pers.dto.ClientCreateDto;
import com.pers.entity.Client;
import com.pers.enums.Role;
import com.pers.enums.Status;
import com.pers.entity.User;
import com.pers.integration.BaseIntegrationIT;
import com.pers.service.ClientService;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@RequiredArgsConstructor
class ClientServiceIT extends BaseIntegrationIT {

    private final ClientService clientService;
    private User user;
    private Client client;
    private ClientCreateDto clientCreateDto;

    @BeforeEach
    void prepare() {
        user = User.builder()
                .login("user10@mail.ru")
                .password("123")
                .role(Role.USER)
                .build();

        var user2 = User.builder()
                .login("admin@mail.ru")
                .password("123")
                .role(Role.ADMIN)
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

        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(client);

        clientCreateDto = new ClientCreateDto(user2.getId(), client.getBalance(), client.getFirstName(), "Ivanov",
                "89632584854", client.getStatus(), client.getCreatedTime());

    }

    @Test
    void findFirstAndLastNameByClientId() {
        var result = clientService.findFirstAndLastNameByClientId(client.getId());

        assertThat(result).isEqualTo("Petr Petrov");
    }

    @Test
    void findById() {
        var result = clientService.findById(client.getId());

        assertThat(result).isPresent();
    }

    @Test
    void create() {
        var result = clientService.create(clientCreateDto);

        assertThat(result.getFirstName()).isEqualTo("Petr");
    }

    @Test
    void update() {
        var result = clientService.update(client.getId(), clientCreateDto);

        assertThat(result).isPresent();
        result.ifPresent(client ->
                assertAll(() -> {
                    assertThat(client.getLastName()).isEqualTo("Ivanov"); // debug
                }));
    }

    @Test
    void findByUserName() {
        var result = clientService.findByUserName(client.getUser().getLogin());

        assertThat(result).isPresent();
    }
}


