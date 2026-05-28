package com.pers.integration.repository;

import com.pers.dto.filter.TransferFilterDto;
import com.pers.entity.Card;
import com.pers.entity.Client;
import com.pers.enums.Role;
import static com.pers.enums.Status.ACTIVE;
import static com.pers.enums.Status.BLOCKED;
import static com.pers.enums.Status.FAILED;
import static com.pers.enums.Status.SUCCESS;
import com.pers.entity.Transfer;
import com.pers.entity.User;
import com.pers.integration.BaseIntegrationIT;
import com.pers.repository.CardRepository;
import com.pers.repository.ClientRepository;
import com.pers.repository.TransferRepository;
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
class TransferRepositoryIT extends BaseIntegrationIT {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;
    private Transfer transfer;
    private Transfer transfer2;

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
        var card = Card.builder()
                .client(client)
                .balance(new BigDecimal(0))
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(5))
                .status(ACTIVE)
                .build();
        var card2 = Card.builder()
                .client(client2)
                .balance(new BigDecimal(0))
                .createdDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(5))
                .status(BLOCKED)
                .build();
        transfer = Transfer.builder()
                .clientId(client)
                .cardNoFrom(card)
                .cardNoTo(card2)
                .amount(new BigDecimal(250))
                .message("hello")
                .recipient(client2.getFirstName() + client2.getLastName())
                .timeOfTransfer(LocalDateTime.now())
                .status(SUCCESS)
                .build();
        transfer2 = Transfer.builder()
                .clientId(client2)
                .cardNoFrom(card2)
                .cardNoTo(card)
                .amount(new BigDecimal(250))
                .message("hello")
                .recipient(client.getFirstName() + client.getLastName())
                .timeOfTransfer(LocalDateTime.now())
                .status(SUCCESS)
                .build();
        userRepository.save(user);
        userRepository.save(user2);
        clientRepository.save(client);
        clientRepository.save(client2);
        cardRepository.save(card);
        cardRepository.save(card2);
        transferRepository.save(transfer);
        transferRepository.save(transfer2);
    }

    @Test
    void create() {
        var result = transferRepository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(transfer);
    }

    @Test
    void update() {
        transfer.setStatus(FAILED);
        transferRepository.save(transfer);

        assertThat(transfer.getStatus()).isEqualTo(FAILED);
    }

    @Test
    void delete() {
        transferRepository.delete(transfer2);

        var result = transferRepository.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findByFilter() {
        var filter = TransferFilterDto.builder()
                .cardNoTo(transfer.getCardNoTo().getId())
                .build();
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        var result = transferRepository.findAllByFilter(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findById() {
        var result = transferRepository.findById(transfer2.getId());

        assertThat(result).isPresent();
    }
}