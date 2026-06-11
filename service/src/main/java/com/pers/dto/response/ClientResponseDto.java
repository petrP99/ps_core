package com.pers.dto.response;

import com.pers.enums.Status;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class ClientResponseDto {

    UUID id;
    BigDecimal balance;
    String firstName;
    String lastName;
    String phone;
    Status status;
    LocalDateTime createdTime;
}
