package com.pers.mapper;

import com.pers.dto.PaymentReadDto;
import com.pers.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentReadMapper implements Mapper<Payment, PaymentReadDto> {

    @Override
    public PaymentReadDto mapFrom(Payment object) {
        return new PaymentReadDto(
                object.getId(),
                object.getShopName(),
                object.getAmount(),
                object.getClientId(),
                object.getCardId(),
                object.getTimeOfPay(),
                object.getStatus()
        );
    }
}