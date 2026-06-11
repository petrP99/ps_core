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
import java.util.UUID;

/**
 * Банковский счёт клиента.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@Builder
@Entity
public class Account implements BaseEntity<UUID> {

    /**
     * Уникальный идентификатор счёта.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Текущий баланс счёта.
     */
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal balance;

    /**
     * Валюта счёта.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    /**
     * Идентификатор клиента-владельца счёта.
     */
    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    /**
     * Наименование счёта.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Кэшбэк в процентах.
     */
    @Column(nullable = true)
    private Integer cashback;

    /**
     * Статус счёта.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
}