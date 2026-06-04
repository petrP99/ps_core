package com.pers.mapper;

import com.pers.dto.AccountCreateDto;
import com.pers.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

import static com.pers.util.constant.Constants.ACCOUNT_NAME;

@Component
@RequiredArgsConstructor
public class AccountCreateMapper {

    public Account toEntity(AccountCreateDto dto, UUID clientId) {
        return Account.builder()
                .balance(BigDecimal.ZERO)
                .currency(dto.currency())
                .clientId(clientId)
                .name(dto.name() != null && !dto.name().isBlank() ? dto.name() : ACCOUNT_NAME)
                .cashback(0)
                .build();
    }
}