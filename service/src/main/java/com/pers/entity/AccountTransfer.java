package com.pers.entity;

import com.pers.enums.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Перевод денежных средств между счетами одного клиента.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "account_transfer")
public class AccountTransfer implements BaseEntity<UUID> {

    /**
     * Уникальный идентификатор перевода.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Идентификатор клиента, совершившего перевод.
     */
    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    /**
     * Идентификатор счёта-отправителя.
     */
    @Column(name = "account_from", nullable = false)
    private UUID accountFrom;

    /**
     * Наименование счёта-отправителя.
     */
    @Column(name = "account_from_name", nullable = false)
    private String accountFromName;

    /**
     * Идентификатор счёта-получателя.
     */
    @Column(name = "account_to", nullable = false)
    private UUID accountTo;

    /**
     * Наименование счёта-получателя.
     */
    @Column(name = "account_to_name", nullable = false)
    private String accountToName;

    /**
     * Сумма перевода в валюте отправителя.
     */
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    /**
     * Сумма к получению в валюте получателя.
     */
    @Column(name = "amount_to", precision = 19, scale = 2, nullable = false)
    private BigDecimal amountTo;

    /**
     * Обменный курс при переводе.
     */
    @Column(name = "exchange_rate", precision = 19, scale = 6, nullable = false)
    private BigDecimal exchangeRate;

    /**
     * Валюта счёта-отправителя.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    /**
     * Валюта счёта-получателя.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_currency", nullable = false)
    private Currency targetCurrency;

    /**
     * Дата и время совершения перевода.
     */
    @Column(name = "time_of_transfer", nullable = false)
    private LocalDateTime timeOfTransfer;
}