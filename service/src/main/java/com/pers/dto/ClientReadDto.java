package com.pers.dto;

import com.pers.enums.Status;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Value
public class ClientReadDto {

    Long id;
    UserReadDto userId;
    BigDecimal balance;
    String firstName;
    String lastName;
    String phone;
    Status status;
    LocalDateTime createdTime;
}