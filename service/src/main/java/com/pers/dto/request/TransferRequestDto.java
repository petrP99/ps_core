package com.pers.dto.request;

import com.pers.enums.Currency;
import com.pers.enums.Status;
import com.pers.validation.TransferInfo;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@TransferInfo
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequestDto {

    @Positive
    private BigDecimal amount;
    private UUID fromClientId;
    private UUID toClientId;
    private String cardFrom;
    private String cardTo;
    private LocalDateTime time;
    private String recipient;
    private String message;
    private Status status;
    private Long id;

    // Сумма, которую МЫ ДОБАВИЛИ на счет получателя
    private BigDecimal amountTo;

    private Currency currency;
    private Currency targetCurrency;
}