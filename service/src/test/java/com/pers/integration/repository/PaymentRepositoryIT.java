package com.pers.integration.repository;

import com.pers.dto.filter.PaymentFilterDto;
import com.pers.entity.Card;
import com.pers.entity.Client;
import com.pers.entity.Payment;
import com.pers.enums.Role;
import static com.pers.enums.Status.ACTIVE;
import static com.pers.enums.Status.BLOCKED;
import static com.pers.enums.Status.FAILED;
import static com.pers.enums.Status.SUCCESS;
import com.pers.entity.User;
import com.pers.integration.BaseIntegrationIT;
import com.pers.repository.CardRepository;
import com.pers.repository.ClientRepository;
import com.pers.repository.PaymentRepository;
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
class PaymentRepositoryIT extends BaseIntegrationIT {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final PaymentRepository paymentRepository;
    private Payment payment;
    private Payment payment2;

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
        payment = Payment.builder()
                .client(client)
                .card(card)
                .shopName("Ozon")
                .amount(new BigDecimal(100))
                .timeOfPay(LocalDateTime.now())
                .status(SUCCESS)
                .build();
        payment2 = Payment.builder()
                .client(client2)
                .card(card2)
                .shopName("Amazon")
                .amount(new BigDecimal(70))
                .timeOfPay(LocalDateTime.now())
                .status(FAILED)
                .build();
        userRepository.save(user);
        userRepository.save(user2);
        clientRepository.save(client);
        clientRepository.save(client2);
        cardRepository.save(card);
        cardRepository.save(card2);
        paymentRepository.save(payment);
        paymentRepository.save(payment2);
    }

    @Test
    void create() {
        var result = paymentRepository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(payment);
    }

    @Test
    void update() {
        payment.setShopName("Apple");
        paymentRepository.save(payment);

        assertThat(payment.getShopName()).isEqualTo("Apple");
    }

    @Test
    void delete() {
        paymentRepository.delete(payment2);

        var result = paymentRepository.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findByFilter() {
        var filter = PaymentFilterDto.builder()
                .shopName("ozon")
                .build();
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        var result = paymentRepository.findAllByFilter(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

}