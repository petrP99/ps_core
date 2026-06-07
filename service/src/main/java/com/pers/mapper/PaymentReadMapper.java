package com.pers.mapper;

import com.pers.dto.response.PaymentResponseDto;
import com.pers.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentReadMapper {

    public PaymentResponseDto toDto(Payment object) {
        return new PaymentResponseDto(
                object.getId(),
                object.getShopName(),
                object.getAmount(),
                object.getClientId(),
                object.getCardNo(),
                object.getTimeOfPay(),
                object.getStatus()
        );
    }
}