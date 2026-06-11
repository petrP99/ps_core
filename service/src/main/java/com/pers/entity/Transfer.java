package com.pers.entity;

import com.pers.enums.Currency;
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
 * Перевод денежных средств между картами разных клиентов.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "cardFrom", "cardTo"})
@ToString
@Builder
@Entity
public class Transfer implements BaseEntity<UUID> {

    /**
     * Уникальный идентификатор перевода.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Идентификатор клиента-отправителя.
     */
    @Column(name = "from_client_id")
    private UUID fromClientId;

    /**
     * Идентификатор клиента-получателя.
     */
    @Column(name = "to_client_id")
    private UUID toClientId;

    /**
     * Номер карты отправителя.
     */
    @Column(name = "card_from")
    private String cardFrom;

    /**
     * Номер карты получателя.
     */
    @Column(name = "card_to")
    private String cardTo;

    /**
     * Сумма перевода в валюте отправителя.
     */
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    /**
     * Сумма к получению в валюте получателя.
     */
    @Column(precision = 19, scale = 2)
    private BigDecimal amountTo;

    /**
     * Обменный курс при переводе.
     */
    @Column(name = "exchange_rate", precision = 19, scale = 6)
    private BigDecimal exchangeRate;

    /**
     * Сумма комиссии за перевод.
     */
    @Column(precision = 19, scale = 2)
    private BigDecimal commission;

    /**
     * Сумма списания со счёта отправителя.
     */
    @Column(name = "debit_amount", precision = 19, scale = 2)
    private BigDecimal debitAmount;

    /**
     * Валюта карты отправителя.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    /**
     * Валюта карты получателя.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_currency")
    private Currency targetCurrency;

    /**
     * Статус перевода.
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Дата и время совершения перевода.
     */
    private LocalDateTime timeOfTransfer;

    /**
     * ФИО получателя перевода.
     */
    private String recipient;

    /**
     * Номер телефона получателя.
     */
    private String recipientPhone;

    /**
     * Сообщение к переводу.
     */
    private String message;
}