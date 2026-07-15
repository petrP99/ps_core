package com.pers.mapper;

import com.pers.dto.response.PaymentResponseDto;
import com.pers.entity.Account;
import com.pers.entity.Payment;
import com.pers.exception.ErrorCode;
import com.pers.exception.PaymentException;
import com.pers.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
@RequiredArgsConstructor
public class PaymentReadMapper {

    private final AccountRepository accountRepository;

    public PaymentResponseDto toDto(Payment object) {
        Account account = accountRepository.findById(object.getAccountId())
                .orElseThrow(() -> new PaymentException(
                        INTERNAL_SERVER_ERROR,
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        object.getAccountId()
                ));
        return new PaymentResponseDto(
                object.getId(),
                object.getClientId(),
                object.getAccountId(),
                account.getName(),
                account.getCurrency(),
                object.getRecipient(),
                object.getPaymentDestination(),
                object.getAmount(),
                object.getTimeOfPay(),
                object.getStatus()
        );
    }
}
