package com.pers.mapper;

import com.pers.dto.AccountCreateDto;
import com.pers.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountCreateMapper {

    public Account mapFrom(AccountCreateDto dto, UUID clientId) {
        return Account.builder()
                .balance(BigDecimal.ZERO)
                .currency(dto.currency())
                .clientId(clientId)
                .name(dto.name() != null && !dto.name().isBlank() ? dto.name() : "Счет")
                .cashback(0)
                .build();
    }
}