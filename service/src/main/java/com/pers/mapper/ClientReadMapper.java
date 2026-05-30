package com.pers.mapper;

import com.pers.dto.ClientReadDto;
import com.pers.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientReadMapper implements Mapper<Client, ClientReadDto> {

    @Override
    public ClientReadDto mapFrom(Client object) {
        return new ClientReadDto(
                object.getId(),
                object.getRole(),
                object.getBalance(),
                object.getFirstName(),
                object.getLastName(),
                object.getPhone(),
                object.getStatus(),
                object.getCreatedTime()
        );
    }
}