package com.pers.mapper;

import com.pers.dto.response.ReplenishmentResponseDto;
import com.pers.entity.Replenishment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReplenishmentReadMapper implements Mapper<Replenishment, ReplenishmentResponseDto> {

    @Override
    public ReplenishmentResponseDto toEntity(Replenishment object) {
        return new ReplenishmentResponseDto(
                object.getId(),
                object.getClientId(),
                object.getCardNo(),
                object.getAmount(),
                object.getTimeOfReplenishment(),
                object.getStatus()
        );
    }
}