package com.pers.mapper;

import com.pers.dto.request.AccountRequestDto;
import com.pers.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

import static com.pers.enums.Status.ACTIVE;
import static com.pers.util.constant.Constants.ACCOUNT_NAME;
import static com.pers.util.constant.Constants.CASHBACK_DEFAULT_VALUE;

@Component
@RequiredArgsConstructor
public class AccountCreateMapper {

    public Account toEntity(AccountRequestDto dto, UUID clientId) {
        String name = dto.name() != null && !dto.name().isBlank()
                ? dto.name()
                : ACCOUNT_NAME.concat(String.valueOf(dto.hashCode()).substring(5));

        return Account.builder()
                .balance(BigDecimal.ZERO)
                .currency(dto.currency())
                .clientId(clientId)
                .name(name)
                .cashback(CASHBACK_DEFAULT_VALUE)
                .status(ACTIVE)
                .build();
    }
}
