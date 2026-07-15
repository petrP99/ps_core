package com.pers.dto.request;

import com.pers.validation.ClientInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

@ClientInfo
@FieldNameConstants
@Builder
public record ClientRequestDto(
        @NotBlank
        @Pattern(regexp = "[а-яА-яa-zA-Z]+", message = "{validation.client.first.name.letters}")
        String firstName,
        @NotBlank
        @Pattern(regexp = "[а-яА-яa-zA-Z]+", message = "{validation.client.last.name.letters}")
        String lastName,
        @Size(min = 11, max = 11, message = "{validation.client.phone.size}")
        String phone) {
}
