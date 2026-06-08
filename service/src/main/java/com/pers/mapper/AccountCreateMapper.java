package com.pers.mapper;

import com.pers.dto.request.AccountRequestDto;
import com.pers.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

import static com.pers.util.constant.Constants.ACCOUNT_NAME;
import static com.pers.enums.AccountStatus.ACTIVE;

@Component
@RequiredArgsConstructor
public class AccountCreateMapper {

    public Account toEntity(AccountRequestDto dto, UUID clientId) {
        return Account.builder()
                .balance(BigDecimal.ZERO)
                .currency(dto.currency())
                .clientId(clientId)
                .name(dto.name() != null && !dto.name().isBlank() ? dto.name() : ACCOUNT_NAME)
                .cashback(0)
                .status(ACTIVE)
                .build();
    }
}
