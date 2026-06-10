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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "account_transfer")
public class AccountTransfer implements BaseEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "account_from", nullable = false)
    private UUID accountFrom;

    @Column(name = "account_from_name", nullable = false)
    private String accountFromName;

    @Column(name = "account_to", nullable = false)
    private UUID accountTo;

    @Column(name = "account_to_name", nullable = false)
    private String accountToName;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "amount_to", precision = 19, scale = 2, nullable = false)
    private BigDecimal amountTo;

    @Column(name = "exchange_rate", precision = 19, scale = 6, nullable = false)
    private BigDecimal exchangeRate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_currency", nullable = false)
    private Currency targetCurrency;

    @Column(name = "time_of_transfer", nullable = false)
    private LocalDateTime timeOfTransfer;
}
