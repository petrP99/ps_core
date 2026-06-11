package com.pers.service;

import com.pers.dto.request.AccountRequestDto;
import com.pers.dto.request.ClientRequestDto;
import com.pers.dto.response.ClientResponseDto;
import com.pers.entity.Client;
import com.pers.enums.Currency;
import com.pers.mapper.ClientCreateMapper;
import com.pers.mapper.ClientReadMapper;
import com.pers.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.pers.util.constant.Constants.DEFAULT_ACCOUNT_NAME;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientReadMapper clientReadMapper;
    private final ClientCreateMapper clientCreateMapper;
    private final AccountService accountService;

    public Optional<ClientResponseDto> findById(UUID id) {
        return clientRepository.findById(id)
                .map(client -> {
                    BigDecimal balance = accountService.getClientTotalBalance(client.getId());
                    return clientReadMapper.mapFrom(client, balance);
                });
    }

    @Transactional
    public UUID getIdFromSuccessAuth(Map<String, Object> attributes) {
        ClientRequestDto createDto = clientCreateMapper.mapToDto(attributes);
        AccountRequestDto defaultAccount = new AccountRequestDto(Currency.RUB, DEFAULT_ACCOUNT_NAME);

        return clientRepository.findByPhone(createDto.phone())
                .map(Client::getId)
                .orElseGet(() -> {
                    UUID clientId = create(createDto).getId();
                    accountService.create(defaultAccount, clientId);
                    return clientId;
                });
    }

    private ClientResponseDto create(ClientRequestDto clientDto) {
        Client client = clientRepository.save(clientCreateMapper.toEntity(clientDto));
        BigDecimal balance = accountService.getClientTotalBalance(client.getId());
        return clientReadMapper.mapFrom(client, balance);
    }
}
