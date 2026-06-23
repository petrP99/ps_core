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

import java.time.LocalDate;
import java.util.UUID;

/**
 * Банковская карта клиента.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Entity
public class Card implements BaseEntity<UUID> {

    /**
     * Уникальный идентификатор карты.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Номер карты.
     */
    @Column(name = "card_number", unique = true, length = 16)
    private String cardNumber;

    /**
     * Идентификатор клиента-владельца карты.
     */
    @Column(name = "client_id")
    private UUID clientId;

    /**
     * Идентификатор счёта, к которому привязана карта.
     */
    @Column(name = "account_id")
    private UUID accountId;

    /**
     * Дата создания карты.
     */
    private LocalDate createdDate;

    /**
     * Дата истечения срока действия карты.
     */
    private LocalDate expireDate;

    /**
     * Наименование карты.
     */
    private String name;

    /**
     * Валюта карты.
     */
    @Enumerated(EnumType.STRING)
    private Currency currency;

    /**
     * Статус карты.
     */
    @Enumerated(EnumType.STRING)
    private Status status;
}