package com.pers.mapper;

import com.pers.dto.response.ReplenishmentResponseDto;
import com.pers.entity.Replenishment;
import com.pers.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReplenishmentReadMapper implements Mapper<Replenishment, ReplenishmentResponseDto> {

    private final AccountRepository accountRepository;

    @Override
    public ReplenishmentResponseDto toEntity(Replenishment object) {
        var account = accountRepository.findById(object.getAccountId()).orElse(null);
        return new ReplenishmentResponseDto(
                object.getId(),
                object.getClientId(),
                object.getAccountId(),
                account != null ? account.getName() : null,
                account != null ? account.getCurrency() : null,
                object.getAmount(),
                object.getTimeOfReplenishment(),
                object.getStatus()
        );
    }
}
