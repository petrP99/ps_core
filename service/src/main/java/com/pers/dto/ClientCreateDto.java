package com.pers.dto;

import com.pers.enums.Status;
import com.pers.validation.ClientInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@ClientInfo
@FieldNameConstants
public record ClientCreateDto(Long userId,
                              @PositiveOrZero
                              BigDecimal balance,

                              @NotBlank(message = "")
                              @Pattern(regexp = "[а-яА-яa-zA-Z]+", message = "field 'firstName' only accepts letters")
                              String firstName,

                              @NotBlank(message = "")
                              @Pattern(regexp = "[а-яА-яa-zA-Z]+", message = "field 'lastName' only accepts letters")
                              String lastName,

                              @NotBlank(message = "")
                              @Size(min = 11, max = 11, message = "field 'phone' only accepts numbers length 11")
                              String phone,
                              Status status,
                              LocalDateTime createdTime) {
}
