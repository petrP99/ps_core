package com.pers.mapper;

import com.pers.dto.request.ReplenishmentRequestDto;
import com.pers.entity.Replenishment;
import com.pers.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReplenishmentCreateMapper {

    public Replenishment mapFrom(ReplenishmentRequestDto object, Status status, UUID clientId) {
        return Replenishment.builder()
                .clientId(clientId)
                .accountId(object.accountId())
                .amount(object.amount())
                .timeOfReplenishment(LocalDateTime.now())
                .status(status)
                .build();
    }
}
