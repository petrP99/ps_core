package com.pers.integration.service;

import com.pers.dto.TransferCreateDto;
import com.pers.dto.filter.TransferFilterDto;
import com.pers.entity.Card;
import com.pers.entity.Client;
import com.pers.entity.Transfer;
import com.pers.entity.User;
import com.pers.enums.Role;
import com.pers.enums.Status;
import com.pers.integration.BaseIntegrationIT;
import com.pers.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@RequiredArgsConstructor
class TransferServiceIT extends BaseIntegrationIT {

    private final TransferService transferService;
    private Transfer transfer;
    private TransferCreateDto transferCreateDto;
    private TransferCreateDto transferCreateDto2;

    @BeforeEach
    void prepare() {
        var user = User.builder()
                .login("user10@mail.ru")
                .password("123")
                .role(Role.USER)
                .build();

        var user2 = User.builder()
                .login("user11@mail.ru")
                .password("123")
                .role(Role.USER)
                .build();

        var client = Client.builder()
                .user(user)
                .balance(new BigDecimal(0))
                .firstName("Petr")
                .lastName("Petrov")
                .phone("89632587854")
                .createdTime(LocalDateTime.now())
                .status(Status.ACTIVE)
                .build();

        var client2 = Client.builder()
                .user(user2)
                .balance(new BigDecimal(0))
                .firstName("Ivan")
                .lastName("Ivanov")
                .phone("89632557854")
                .createdTime(LocalDateTime.now())
                .status(Status.ACTIVE)
                .build();

        var card = Card.builder()
                .client(client)
                .balance(new BigDecimal(1000))
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(5))
                .status(Status.ACTIVE)
                .build();

        var card2 = Card.builder()
                .client(client2)
                .balance(new BigDecimal(0))
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(5))
                .status(Status.ACTIVE)
                .build();

        transfer = Transfer.builder()
                .clientId(client)
                .cardNoFrom(card)
                .cardNoTo(card2)
                .amount(new BigDecimal(0))
                .message("happy birthday")
                .recipient("Ivan Ivanov")
                .timeOfTransfer(LocalDateTime.now())
                .status(Status.SUCCESS)
                .build();

        var transfer2 = Transfer.builder()
                .clientId(client2)
                .cardNoFrom(card2)
                .cardNoTo(card)
                .amount(new BigDecimal(250))
                .message("for you")
                .recipient("Ivan Ivanov")
                .timeOfTransfer(LocalDateTime.now())
                .status(Status.SUCCESS)
                .build();

        entityManager.persist(user);
        entityManager.persist(user2);
        entityManager.persist(client);
        entityManager.persist(client2);
        entityManager.persist(card);
        entityManager.persist(card2);
        entityManager.persist(transfer);
        entityManager.persist(transfer2);

        transferCreateDto = TransferCreateDto.builder()
                .clientId(transfer.getClientId().getId())
                .cardIdFrom(transfer.getCardNoFrom().getId())
                .cardIdTo(transfer.getCardNoTo().getId())
                .amount(transfer.getAmount())
                .time(transfer.getTimeOfTransfer())
                .recipient(transfer.getRecipient())
                .message(transfer.getMessage())
                .status(transfer.getStatus())
                .build();

        transferCreateDto2 = TransferCreateDto.builder()
                .clientId(transfer2.getClientId().getId())
                .cardIdFrom(transfer2.getCardNoFrom().getId())
                .cardIdTo(transfer2.getCardNoTo().getId())
                .amount(transfer2.getAmount())
                .time(transfer2.getTimeOfTransfer())
                .recipient(transfer2.getRecipient())
                .message(transfer2.getMessage())
                .status(transfer2.getStatus())
                .build();
    }

    @Test
    void checkAndCreateTransfer() {
        var result = transferService.checkAndCreateTransfer(transferCreateDto2);

        assertFalse(result);
    }

    @Test
    void findById() {
        var result = transferService.findById(transfer.getId());

        assertThat(result).isPresent();
    }

    @Test
    void create() {
        var result = transferService.create(transferCreateDto);

        assertThat(result.recipient()).isEqualTo("Ivan Ivanov");
    }

    @Test
    void findAllByFilter() {
        var filter = new TransferFilterDto(null, null, null, null, null, null, null, "happy", null);
        var result = transferService.findAllByFilter(filter, Pageable.ofSize(1));

        assertThat(result).hasSize(1);
    }

    @Test
    void findAllByClientByFilter() {
        var filter = new TransferFilterDto(null, null, null, null, null, null, null, "happy", null);
        var result = transferService.findAllByClientByFilter(filter, Pageable.ofSize(1), transfer.getClientId().getId());

        assertThat(result).hasSize(1);
    }
}
