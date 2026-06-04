package com.pers.service;

import com.pers.dto.AccountCreateDto;
import com.pers.dto.ClientCreateDto;
import com.pers.dto.ClientReadDto;
import com.pers.dto.ClientUpdateBalanceDto;
import com.pers.dto.filter.ClientFilterDto;
import com.pers.entity.Client;
import com.pers.enums.Currency;
import com.pers.mapper.ClientCreateMapper;
import com.pers.mapper.ClientReadMapper;
import com.pers.mapper.ClientUpdateBalanceMapper;
import com.pers.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.pers.util.constant.Constants.ACCOUNT_NAME;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientReadMapper clientReadMapper;
    private final ClientCreateMapper clientCreateMapper;
    private final ClientUpdateBalanceMapper clientUpdateBalanceMapper;
    private final AccountService accountService;

    public Page<ClientReadDto> findAll(ClientFilterDto filter, Pageable pageable) {
        return clientRepository.findAllByFilter(filter, pageable)
                .map(client -> {
                    BigDecimal balance = accountService.getClientTotalBalance(client.getId());
                    return clientReadMapper.mapFrom(client, balance);
                });
    }

    public String findFirstAndLastNameByClientId(UUID id) {
        return clientRepository.findFirstAndLastNameByClientId(id);
    }

    public Optional<ClientReadDto> findById(UUID id) {
        return clientRepository.findById(id)
                .map(client -> {
                    BigDecimal balance = accountService.getClientTotalBalance(client.getId());
                    return clientReadMapper.mapFrom(client, balance);
                });
    }

    @Transactional
    public ClientReadDto create(ClientCreateDto clientDto) {
        return Optional.of(clientDto)
                .map(clientCreateMapper::mapFrom)
                .map(clientRepository::save)
                .map(client -> {
                    BigDecimal balance = accountService.getClientTotalBalance(client.getId());
                    return clientReadMapper.mapFrom(client, balance);
                })
                .orElseThrow();
    }

    @Transactional
    public Optional<ClientReadDto> update(UUID id, ClientCreateDto clientDto) {
        return clientRepository.findById(id)
                .map(entity -> clientCreateMapper.map(clientDto, entity))
                .map(clientRepository::saveAndFlush)
                .map(client -> {
                    BigDecimal balance = accountService.getClientTotalBalance(client.getId());
                    return clientReadMapper.mapFrom(client, balance);
                });
    }

    @Transactional
    public ClientReadDto updateBalance(ClientUpdateBalanceDto clientDto) {
        return Optional.of(clientDto)
                .map(clientUpdateBalanceMapper::mapFrom)
                .map(clientRepository::saveAndFlush)
                .map(client -> {
                    BigDecimal balance = accountService.getClientTotalBalance(client.getId());
                    return clientReadMapper.mapFrom(client, balance);
                })
                .orElseThrow();
    }

    @Transactional
    public boolean delete(UUID id) {
        return clientRepository.findById(id)
                .map(entity -> {
                    clientRepository.delete(entity);
                    clientRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    public Optional<ClientReadDto> findByPhone(String phone) {
        return clientRepository.findByPhone(phone)
                .map(client -> {
                    BigDecimal balance = accountService.getClientTotalBalance(client.getId());
                    return clientReadMapper.mapFrom(client, balance);
                });
    }

    @Transactional
    public UUID getIdFromSuccessAuth(Map<String, Object> attributes) {
        ClientCreateDto createDto = clientCreateMapper.mapToDto(attributes);
        AccountCreateDto defaultAccount = new AccountCreateDto(Currency.RUB, ACCOUNT_NAME);

        return clientRepository.findByPhone(createDto.phone())
                .map(Client::getId)
                .orElseGet(() -> {
                    UUID clientId = create(createDto).getId();
                    accountService.create(defaultAccount, clientId);
                    return clientId;
                });
    }
}
