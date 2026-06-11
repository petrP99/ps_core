package com.pers.mapper;

import com.pers.dto.request.PaymentRequestDto;
import com.pers.entity.Payment;
import com.pers.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PaymentCreateMapper {

    public Payment toEntity(PaymentRequestDto object, UUID clientId, Status status) {
        return Payment.builder()
                .paymentDestination(object.paymentDestination())
                .amount(object.amount())
                .clientId(clientId)
                .accountId(object.accountId())
                .recipient(object.recipient())
                .timeOfPay(LocalDateTime.now())
                .status(status)
                .build();
    }
}
