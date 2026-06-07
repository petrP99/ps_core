package com.pers.mapper;

import com.pers.dto.request.PaymentRequestDto;
import com.pers.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.pers.enums.Status.FAILED;
import static com.pers.enums.Status.SUCCESS;

@Component
@RequiredArgsConstructor
public class PaymentCreateMapper implements Mapper<PaymentRequestDto, Payment> {

    @Override
    public Payment toEntity(PaymentRequestDto object) {
        return Payment.builder()
                .shopName((object.shopName()))
                .amount(object.amount())
                .clientId(object.clientId())
                .cardNo(object.cardNo())
                .timeOfPay(LocalDateTime.now())
                .status(object.status() == null ? SUCCESS : FAILED)
                .build();
    }
}