package com.pers.mapper;

import com.pers.dto.PaymentCreateDto;
import com.pers.entity.Payment;
import static com.pers.enums.Status.FAILED;
import static com.pers.enums.Status.SUCCESS;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentCreateMapper implements Mapper<PaymentCreateDto, Payment> {

    @Override
    public Payment mapFrom(PaymentCreateDto object) {
        return Payment.builder()
                .shopName(object.shopName())
                .amount(object.amount())
                .clientId(object.clientId())
                .cardId(object.cardId())
                .timeOfPay(LocalDateTime.now())
                .status(object.status() == null ? SUCCESS : FAILED)
                .build();
    }
}