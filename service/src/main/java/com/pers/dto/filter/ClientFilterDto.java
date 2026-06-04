package com.pers.dto.filter;

import com.pers.enums.Status;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ClientFilterDto(
        UUID id,
        String firstName,
        String lastName,
        String phone,
        Status status) {
}
