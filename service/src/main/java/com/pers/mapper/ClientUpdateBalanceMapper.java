package com.pers.mapper;

import com.pers.dto.ClientUpdateBalanceDto;
import com.pers.entity.Client;
import com.pers.enums.Status;
import com.pers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ClientUpdateBalanceMapper implements Mapper<ClientUpdateBalanceDto, Client> {

    private final UserRepository userRepository;

    @Override
    public Client mapFrom(ClientUpdateBalanceDto object) {
        return Client.builder()
                .id(object.id())
                .user(userRepository.findById(object.userId()).orElseThrow(IllegalArgumentException::new))
                .balance(object.balance())
                .firstName(object.firstName())
                .lastName(object.lastName())
                .phone(object.phone())
                .status(Status.ACTIVE)
                .createdTime(LocalDateTime.now())
                .build();
    }
}
