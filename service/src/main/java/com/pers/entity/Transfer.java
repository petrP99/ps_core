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

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "cardFrom", "cardTo"})
@ToString
@Builder
@Entity
public class Transfer implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_client_id")
    private UUID fromClientId;

    @Column(name = "to_client_id")
    private UUID toClientId;

    @Column(name = "card_from")
    private String cardFrom;

    @Column(name = "card_to")
    private String cardTo;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(precision = 19, scale = 2)
    private BigDecimal amountTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_currency")
    private Currency targetCurrency;

    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime timeOfTransfer;
    private String recipient;
    private String message;
}