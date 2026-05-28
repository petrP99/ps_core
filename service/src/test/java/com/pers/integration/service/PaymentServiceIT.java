package com.pers.integration.service;

import com.pers.dto.PaymentCreateDto;
import com.pers.dto.filter.PaymentFilterDto;
import com.pers.entity.Card;
import com.pers.entity.Client;
import com.pers.entity.Payment;
import com.pers.enums.Role;
import com.pers.enums.Status;
import com.pers.entity.User;
import com.pers.integration.BaseIntegrationIT;
import com.pers.service.PaymentService;
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
class PaymentServiceIT extends BaseIntegrationIT {

    private final PaymentService paymentService;
    private User user;
    private Client client;
    private Card card;
    private Payment payment;
    private PaymentCreateDto paymentCreateDto;

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

        payment = Payment.builder()
                .client(client)
                .card(card)
                .amount(new BigDecimal(250))
                .shopName("Ozon")
                .timeOfPay(LocalDateTime.now())
                .status(Status.SUCCESS)
                .build();

        var payment2 = Payment.builder()
                .client(client)
                .card(card)
                .amount(new BigDecimal(200))
                .shopName("Ozon")
                .timeOfPay(LocalDateTime.now())
                .status(Status.SUCCESS)
                .build();

        entityManager.persist(user);
        entityManager.persist(client);
        entityManager.persist(card);
        entityManager.persist(card2);
        entityManager.persist(payment);

        paymentCreateDto = new PaymentCreateDto(
                payment.getShopName(),
                payment.getAmount(),
                payment.getClient().getId(),
                payment.getCard().getId(),
                payment.getTimeOfPay(),
                payment.getStatus());
    }

    @Test
    void checkAndCreatePayment() {
        var result = paymentService.checkAndCreatePayment(paymentCreateDto);

        assertTrue(result);
    }

    @Test
    void findById() {
        var result = paymentService.findById(payment.getId());

        assertThat(result).isPresent();
    }

    @Test
    void create() {
        var result = paymentService.create(paymentCreateDto);

        assertThat(result.shopName()).isEqualTo("Ozon");
    }

    @Test
    void findAllByClientByFilter() {
        var filter = new PaymentFilterDto(null, "Oz", null, null, null, null, null);
        var result = paymentService.findAllByClientByFilter(filter, Pageable.ofSize(1), payment.getClient().getId());

        assertThat(result).hasSize(1);
    }

}
