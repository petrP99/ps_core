package com.pers.mapper;

import com.pers.dto.ClientReadDto;
import com.pers.entity.Client;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ClientReadMapper implements Mapper<Client, ClientReadDto> {

    @Override
    public ClientReadDto mapFrom(Client object) {
        return mapFrom(object, null);
    }

    public ClientReadDto mapFrom(Client object, BigDecimal balance) {
        return new ClientReadDto(
                object.getId(),
                object.getRole(),
                balance,
                object.getFirstName(),
                object.getLastName(),
                object.getPhone(),
                object.getStatus(),
                object.getCreatedTime()
        );
    }
}
