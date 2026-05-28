package com.pers.entity;


import com.pers.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "clientTo", "cardIdTo"})
@ToString(exclude = {"clientTo", "cardIdTo"})
@Builder
@Entity
public class Replenishment implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_to")
    private Client clientTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_no_to")
    private Card cardNoTo;

    private BigDecimal amount;
    private LocalDateTime timeOfReplenishment;

    @Enumerated(EnumType.STRING)
    private Status status;


}
