package com.pers.entity;

import com.pers.enums.Currency;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Обменный курс валюты.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ExchangeRate {

    /**
     * Код валюты.
     */
    @Id
    @Enumerated(EnumType.STRING)
    private Currency currencyCode;

    /**
     * Текущий обменный курс.
     */
    private BigDecimal rate;

    /**
     * Дата последнего обновления курса.
     */
    private LocalDate updatedAt;
}