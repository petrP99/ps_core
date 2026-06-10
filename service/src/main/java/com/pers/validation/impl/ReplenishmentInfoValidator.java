package com.pers.validation.impl;

import com.pers.dto.request.ReplenishmentRequestDto;
import com.pers.validation.ReplenishmentInfo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static org.springframework.util.StringUtils.hasText;

public class ReplenishmentInfoValidator implements ConstraintValidator<ReplenishmentInfo, ReplenishmentRequestDto> {
    @Override
    public boolean isValid(ReplenishmentRequestDto value, ConstraintValidatorContext context) {
        return value != null && value.amount() != null && value.accountId() != null;
    }
}
