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

    private UUID fromClientId;
    private UUID toClientId;

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "Номер карты отправителя должен содержать 16 цифр")
    private String cardFrom;

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "Номер карты получателя должен содержать 16 цифр")
    private String cardTo;

    private LocalDateTime time;

    @Size(max = 120)
    private String recipient;

    private String recipientPhone;

    @Size(max = 120)
    private String message;
    private Status status;
    private BigDecimal amountTo;
    private BigDecimal exchangeRate;
    private BigDecimal commission;
    private BigDecimal debitAmount;
    private Currency currency;
    private Currency targetCurrency;
}
