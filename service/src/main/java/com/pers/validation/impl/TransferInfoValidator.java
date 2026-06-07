package com.pers.validation.impl;

import com.pers.dto.request.TransferRequestDto;
import com.pers.validation.TransferInfo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TransferInfoValidator implements ConstraintValidator<TransferInfo, TransferRequestDto> {


    @Override
    public boolean isValid(TransferRequestDto value, ConstraintValidatorContext context) {
//        return value.amount().compareTo(BigDecimal.ZERO) > 0  && value.cardIdTo() != 0;
        return true;
    }
}
