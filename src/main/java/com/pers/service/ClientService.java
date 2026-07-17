package com.pers.service;

import com.pers.dto.request.AccountRequestDto;
import com.pers.dto.request.CardRequestDto;
import com.pers.dto.request.ClientRequestDto;
import com.pers.dto.response.ClientResponseDto;
import com.pers.entity.Client;
import com.pers.enums.Currency;
import com.pers.mapper.ClientCreateMapper;
import com.pers.mapper.ClientReadMapper;
import com.pers.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.pers.util.constant.Constants.DEFAULT_ACCOUNT_NAME;
import static com.pers.util.constant.Constants.DEFAULT_CARD_NAME;
import static com.pers.util.constant.Constants.KEYCLOAK_SUB;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientReadMapper clientReadMapper;
    private final ClientCreateMapper clientCreateMapper;
    private final AccountService accountService;
    private final CardService cardService;

    public Optional<ClientResponseDto> findById(UUID id) {
        return clientRepository.findById(id)
                .map(client -> {
                    BigDecimal balance = accountService.getClientTotalBalance(client.getId());
                    return clientReadMapper.mapFrom(client, balance);
                });
    }

    @Transactional
    public UUID getIdFromSuccessAuth(Map<String, Object> attributes) {
        UUID clientId = extractSubjectClientId(attributes);
        if (clientRepository.existsById(clientId)) {
            return clientId;
        }

        ClientRequestDto createDto = clientCreateMapper.mapToDto(attributes);
        try {
            return createClientWithDefaults(createDto, clientId);
        } catch (DataIntegrityViolationException exception) {
            if (clientRepository.existsById(clientId)) {
                return clientId;
            }
            throw exception;
        }
    }

    private UUID createClientWithDefaults(ClientRequestDto clientDto, UUID clientId) {
        AccountRequestDto defaultAccount = new AccountRequestDto(Currency.RUB, DEFAULT_ACCOUNT_NAME);
        ClientResponseDto createdClient = create(clientDto, clientId);
        var createdAccount = accountService.create(defaultAccount, createdClient.getId());
        CardRequestDto defaultCard = new CardRequestDto(
                DEFAULT_CARD_NAME,
                Currency.RUB,
                false,
                createdAccount.id()
        );
        cardService.create(createdClient.getId(), defaultCard);
        return createdClient.getId();
    }

    private UUID extractSubjectClientId(Map<String, Object> attributes) {
        Object subject = attributes.get(KEYCLOAK_SUB);
        if (subject instanceof String value && !value.isBlank()) {
            return UUID.fromString(value);
        }
        throw new IllegalStateException("JWT claim sub is missing or empty");
    }

    private ClientResponseDto create(ClientRequestDto clientDto, UUID clientId) {
        Client newClient = clientCreateMapper.toEntity(clientDto);
        if (clientId != null) {
            newClient.setId(clientId);
        }
        Client client = clientRepository.saveAndFlush(newClient);
        BigDecimal balance = accountService.getClientTotalBalance(client.getId());
        return clientReadMapper.mapFrom(client, balance);
    }
}
