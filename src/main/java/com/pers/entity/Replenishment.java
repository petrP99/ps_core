package com.pers.entity;


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
 * Пополнение банковского счёта клиента.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@Builder
@Entity
public class Replenishment implements BaseEntity<UUID> {

    /**
     * Уникальный идентификатор пополнения.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Идентификатор клиента, пополнившего счёт.
     */
    @Column(name = "client_id")
    private UUID clientId;

    /**
     * Идентификатор пополняемого счёта.
     */
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    /**
     * Сумма пополнения.
     */
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    /**
     * Дата и время пополнения.
     */
    private LocalDateTime timeOfReplenishment;

    /**
     * Статус пополнения.
     */
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Внешний идентификатор операции для идемпотентных внутренних пополнений.
     */
    @Column(name = "external_operation_id", unique = true)
    private UUID externalOperationId;

    /**
     * Источник пополнения: ручное пополнение, кешбэк и т.д.
     */
    @Column(name = "source_type", nullable = false)
    private String sourceType;

    /**
     * Описание пополнения для истории операций.
     */
    @Column
    private String description;
}
