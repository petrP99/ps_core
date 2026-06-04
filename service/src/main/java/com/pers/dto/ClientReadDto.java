package com.pers.dto;

import com.pers.enums.Role;
import com.pers.enums.Status;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class ClientReadDto {

    UUID id;
    Role role;
    BigDecimal balance;
    String firstName;
    String lastName;
    String phone;
    Status status;
    LocalDateTime createdTime;
}