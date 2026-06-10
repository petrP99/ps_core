package com.pers.validation.impl;

import com.pers.dto.request.PaymentRequestDto;
import com.pers.validation.PaymentInfo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PaymentInfoValidator implements ConstraintValidator<PaymentInfo, PaymentRequestDto> {
    @Override
    public boolean isValid(PaymentRequestDto value, ConstraintValidatorContext context) {
        return value != null
               && value.amount() != null
               && value.accountId() != null
               && value.recipient() != null
               && value.paymentDestination() != null
               && !value.paymentDestination().isBlank();
    }
}
