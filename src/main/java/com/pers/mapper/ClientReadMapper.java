package com.pers.mapper;

import com.pers.dto.response.ClientResponseDto;
import com.pers.entity.Client;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ClientReadMapper implements Mapper<Client, ClientResponseDto> {

    @Override
    public ClientResponseDto toEntity(Client object) {
        return mapFrom(object, null);
    }

    public ClientResponseDto mapFrom(Client object, BigDecimal balance) {
        return new ClientResponseDto(
                object.getId(),
                balance,
                object.getFirstName(),
                object.getLastName(),
                object.getPhone(),
                object.getStatus(),
                object.getCreatedTime()
        );
    }
}
