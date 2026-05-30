package com.pers.mapper;

import com.pers.dto.ClientUpdateBalanceDto;
import com.pers.entity.Client;
import com.pers.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ClientUpdateBalanceMapper implements Mapper<ClientUpdateBalanceDto, Client> {

    @Override
    public Client mapFrom(ClientUpdateBalanceDto object) {
        return Client.builder()
                .id(object.id())
                .balance(object.balance())
                .firstName(object.firstName())
                .lastName(object.lastName())
                .phone(object.phone())
                .status(Status.ACTIVE)
                .createdTime(LocalDateTime.now())
                .build();
    }
}