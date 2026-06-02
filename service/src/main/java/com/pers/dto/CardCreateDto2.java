package com.pers.dto;

import com.pers.enums.Currency;
import jakarta.validation.constraints.Size;

public record CardCreateDto2(@Size(max = 50)
                             String name,
                             Currency currency) {
}
