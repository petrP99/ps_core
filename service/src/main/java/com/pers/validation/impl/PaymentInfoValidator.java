package com.pers.validation.impl;

import com.pers.dto.request.PaymentRequestDto;
import com.pers.validation.PaymentInfo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static org.springframework.util.StringUtils.hasText;

public class PaymentInfoValidator implements ConstraintValidator<PaymentInfo, PaymentRequestDto> {
    @Override
    public boolean isValid(PaymentRequestDto value, ConstraintValidatorContext context) {
        return hasText(String.valueOf(value.amount())) && hasText(value.cardNo());
    }
}
