package com.pers.mapper;

import com.pers.dto.request.ClientRequestDto;
import com.pers.entity.Client;
import com.pers.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

import static com.pers.enums.Status.ACTIVE;
import static com.pers.enums.Status.BLOCKED;

@Component
@RequiredArgsConstructor
public class ClientCreateMapper implements Mapper<ClientRequestDto, Client> {

    @Override
    public Client toEntity(ClientRequestDto object) {
        return Client.builder()
                .role(Role.USER)
                .firstName(object.firstName())
                .lastName(object.lastName())
                .phone(object.phone())
                .status(ACTIVE)
                .createdTime(LocalDateTime.now())
                .build();
    }

    @Override
    public Client map(ClientRequestDto fromObject, Client toObject) {
        toObject.setFirstName(fromObject.firstName());
        toObject.setLastName(fromObject.lastName());
        toObject.setPhone(fromObject.phone());
        toObject.setStatus(toObject.getStatus() == ACTIVE ? BLOCKED : ACTIVE);
        toObject.setCreatedTime(LocalDateTime.now());
        return toObject;
    }

    public ClientRequestDto mapToDto(Client client) {
        return ClientRequestDto.builder()
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .phone(client.getPhone())
                .build();
    }

    public ClientRequestDto mapToDto(Map<String, Object> attributes) {
        return ClientRequestDto.builder()
                .firstName(attributes.get("name").toString())
                .lastName(attributes.get("family_name").toString())
                .phone(attributes.get("phone").toString())
                .build();
    }
}
