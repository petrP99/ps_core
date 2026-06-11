package com.pers.entity;

import com.pers.enums.PaymentRecipient;
import com.pers.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Платёж, совершённый клиентом.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Entity
public class Payment implements BaseEntity<UUID> {

    /**
     * Уникальный идентификатор платежа.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Идентификатор клиента, совершившего платёж.
     */
    @Column(name = "client_id")
    private UUID clientId;

    /**
     * Идентификатор счёта, с которого произведён платёж.
     */
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    /**
     * Дата и время совершения платежа.
     */
    private LocalDateTime timeOfPay;

    /**
     * Сумма платежа.
     */
    private BigDecimal amount;

    /**
     * Статус платежа.
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Тип получателя платежа.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentRecipient recipient;

    /**
     * Назначение платежа.
     */
    @Column(name = "payment_destination", nullable = false)
    private String paymentDestination;
}