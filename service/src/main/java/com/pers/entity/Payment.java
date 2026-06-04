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

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "shopName", "cardId"})
@ToString
@Builder
@Entity
public class Payment implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shopName;
    
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "pay_by_client_id")
    private UUID clientId;

    @Column(name = "pay_by_card_no")
    private Long cardId;

    private LocalDateTime timeOfPay;

    @Enumerated(EnumType.STRING)
    private Status status;

}
