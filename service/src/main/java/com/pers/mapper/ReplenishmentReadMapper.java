package com.pers.mapper;

import com.pers.dto.ReplenishmentReadDto;
import com.pers.entity.Replenishment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReplenishmentReadMapper implements Mapper<Replenishment, ReplenishmentReadDto> {

    @Override
    public ReplenishmentReadDto mapFrom(Replenishment object) {
        return new ReplenishmentReadDto(
                object.getId(),
                object.getClientId(),
                object.getCardNoTo(),
                object.getAmount(),
                object.getTimeOfReplenishment(),
                object.getStatus()
        );
    }
}