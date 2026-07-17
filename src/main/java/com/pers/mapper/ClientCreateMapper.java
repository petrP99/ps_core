package com.pers.mapper;

import com.pers.dto.request.ClientRequestDto;
import com.pers.entity.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

import static com.pers.enums.Status.ACTIVE;
import static com.pers.enums.Status.BLOCKED;
import static com.pers.util.constant.Constants.KEYCLOAK_FAMILY_NAME;
import static com.pers.util.constant.Constants.KEYCLOAK_NAME;
import static com.pers.util.constant.Constants.KEYCLOAK_PHONE;

@Component
@RequiredArgsConstructor
public class ClientCreateMapper implements Mapper<ClientRequestDto, Client> {

    @Override
    public Client toEntity(ClientRequestDto object) {
        return Client.builder()
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

    public ClientRequestDto mapToDto(Map<String, Object> attributes) {
        String fullName = attributes.get(KEYCLOAK_NAME).toString().trim();
        String[] nameParts = fullName.split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1
                ? nameParts[1]
                : attributes.get(KEYCLOAK_FAMILY_NAME).toString().trim();

        return ClientRequestDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phone(attributes.get(KEYCLOAK_PHONE).toString())
                .build();
    }
}
