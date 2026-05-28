package com.pers.integration.controller;

import com.pers.dto.ClientCreateDto;
import com.pers.entity.Client;
import com.pers.enums.Role;
import com.pers.enums.Status;
import com.pers.entity.User;
import com.pers.integration.BaseIntegrationIT;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@AutoConfigureMockMvc
class ClientControllerIT extends BaseIntegrationIT {

    private final MockMvc mockMvc;
    private final EntityManager entityManager;
    private User user;
    private Client client;
    private Client client2;
    private ClientCreateDto clientCreateDto;

    @BeforeEach
    void init() {
        List<GrantedAuthority> roles = Arrays.asList(Role.ADMIN, Role.USER, Role.SUPER_ADMIN);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user, user.getPassword(), roles);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(testingAuthenticationToken);
        SecurityContextHolder.setContext(securityContext);

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

    }

    @Test
    void create() throws Exception {
        mockMvc.perform(post("/clients")
                        .param(client.getFirstName(), "Petr")
                        .param(client.getLastName(), "Petrov")
                        .param(client.getPhone(), "89632587854")
                        .param(client.getStatus().name(), "ACTIVE"))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrlPattern("/client/home/" + client.getUser().getId())
                );
    }

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/clients"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void update() throws Exception {
        mockMvc.perform(post("/clients/{id}/update", client.getId())
                        .param(client.getFirstName(), "Petr")
                        .param(client.getPhone(), "89632587854"))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrlPattern("/clients/{id}")
                );
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(post("/users/{id}/delete", client.getId()))
                .andExpectAll(
                        status().is3xxRedirection()
                );
    }

    @Test
    void findById() throws Exception {
        mockMvc.perform(get("/clients/{id}", client.getId()))
                .andExpectAll(
                        status().is2xxSuccessful(),
                        model().attributeExists("client"));
    }
}
