package com.pers.integration.mapper;

import com.pers.dto.ClientCreateDto;
import com.pers.entity.Client;
import com.pers.enums.Role;
import com.pers.enums.Status;
import com.pers.entity.User;
import com.pers.integration.BaseIntegrationIT;
import com.pers.mapper.ClientCreateMapper;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@RequiredArgsConstructor
class ClientCreateMapperIT extends BaseIntegrationIT {

    private final ClientCreateMapper clientCreateMapper;
    private User user;
    private Client client;
    private ClientCreateDto clientCreateDto;

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

        clientCreateDto = new ClientCreateDto(user.getId(),
                new BigDecimal(10),
                "ivan", "ivanov",
                "89774115588", Status.BLOCKED, LocalDateTime.now());
    }

    @Test
    void mapFrom() {
        var result = clientCreateMapper.mapFrom(clientCreateDto);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("ivan");
    }

    @Test
    void map() {
        var result = clientCreateMapper.map(clientCreateDto, client);

        assertThat(result).isNotNull();
        assertThat(result.getStatus().name()).isEqualTo("ACTIVE");
    }
}


















