package com.pers.dto.request;

import com.pers.enums.Currency;
import com.pers.enums.Status;
import com.pers.validation.TransferInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "{validation.card.sender.number}")
    private String cardFrom;

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "{validation.card.recipient.number}")
    private String cardTo;

    @Size(max = 120)
    private String recipient;

    @Size(max = 120)
    private String message;
    private UUID fromClientId;
    private UUID toClientId;
    private String recipientPhone;
    private LocalDateTime time;
    private Status status;
    private BigDecimal amountTo;
    private BigDecimal exchangeRate;
    private BigDecimal commission;
    private BigDecimal debitAmount;
    private Currency currency;
    private Currency targetCurrency;
}
