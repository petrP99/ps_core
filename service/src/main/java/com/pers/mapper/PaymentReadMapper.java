package com.pers.mapper;

import com.pers.dto.response.PaymentResponseDto;
import com.pers.entity.Payment;
import com.pers.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentReadMapper {

    private final AccountRepository accountRepository;

    public PaymentResponseDto toDto(Payment object) {
        var account = accountRepository.findById(object.getAccountId()).orElse(null);
        return new PaymentResponseDto(
                object.getId(),
                object.getClientId(),
                object.getAccountId(),
                account != null ? account.getName() : null,
                account != null ? account.getCurrency() : null,
                object.getRecipient(),
                object.getPaymentDestination(),
                object.getAmount(),
                object.getTimeOfPay(),
                object.getStatus()
        );
    }
}
