package com.pers.dto.filter;

import com.pers.enums.Status;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ClientFilterDto(
        Long id,
        String firstName,
        String lastName,
        BigDecimal balance,
        String phone,
        Status status) {
}