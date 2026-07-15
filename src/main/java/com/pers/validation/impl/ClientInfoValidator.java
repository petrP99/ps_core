package com.pers.validation.impl;

import com.pers.dto.request.ClientRequestDto;
import com.pers.validation.ClientInfo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static org.springframework.util.StringUtils.hasText;

public class ClientInfoValidator implements ConstraintValidator<ClientInfo, ClientRequestDto> {


    @Override
    public boolean isValid(ClientRequestDto value, ConstraintValidatorContext context) {
        return hasText(value.phone()) && hasText(value.firstName()) && hasText(value.lastName());
    }
}
