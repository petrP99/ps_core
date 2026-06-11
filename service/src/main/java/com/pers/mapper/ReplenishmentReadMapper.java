package com.pers.mapper;

import com.pers.dto.response.ReplenishmentResponseDto;
import com.pers.entity.Replenishment;
import com.pers.exception.ErrorCode;
import com.pers.exception.ReplenishmentException;
import com.pers.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
@RequiredArgsConstructor
public class ReplenishmentReadMapper implements Mapper<Replenishment, ReplenishmentResponseDto> {

    private final AccountRepository accountRepository;

    @Override
    public ReplenishmentResponseDto toEntity(Replenishment object) {
        var account = accountRepository.findById(object.getAccountId())
                .orElseThrow(() -> new ReplenishmentException(
                        INTERNAL_SERVER_ERROR,
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        object.getAccountId()
                ));
        return new ReplenishmentResponseDto(
                object.getId(),
                object.getClientId(),
                object.getAccountId(),
                account.getName(),
                account.getCurrency(),
                object.getAmount(),
                object.getTimeOfReplenishment(),
                object.getStatus()
        );
    }
}
