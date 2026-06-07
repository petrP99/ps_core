package com.pers.service;

import com.pers.dto.request.AccountRequestDto;
import com.pers.dto.response.ClientResponseDto;
import com.pers.dto.request.ClientRequestDto;
import com.pers.dto.filter.ClientFilterDto;
import com.pers.entity.Client;
import com.pers.enums.Currency;
import com.pers.mapper.ClientCreateMapper;
import com.pers.mapper.ClientReadMapper;
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
    private final AccountService accountService;

    public Page<ClientResponseDto> findAll(ClientFilterDto filter, Pageable pageable) {
        return clientRepository.findAllByFilter(filter, pageable)
                .map(client -> {
                    BigDecimal balance = accountService.getClientTotalBalance(client.getId());
                    return clientReadMapper.mapFrom(client, balance);
                });
    }

    public String findFirstAndLastNameByClientId(UUID id) {
        return clientRepository.findFirstAndLastNameByClientId(id);
    }

    public Optional<ClientResponseDto> findById(UUID id) {
        return clientRepository.findById(id)
                .map(client -> {
                    BigDecimal balance = accountService.getClientTotalBalance(client.getId());
                    return clientReadMapper.mapFrom(client, balance);
                });
    }

    @Transactional
    public ClientResponseDto create(ClientRequestDto clientDto) {
        return Optional.of(clientDto)
                .map(clientCreateMapper::toEntity)
                .map(clientRepository::save)
                .map(client -> {
                    BigDecimal balance = accountService.getClientTotalBalance(client.getId());
                    return clientReadMapper.mapFrom(client, balance);
                })
                .orElseThrow();
    }

    @Transactional
    public Optional<ClientResponseDto> update(UUID id, ClientRequestDto clientDto) {
        return clientRepository.findById(id)
                .map(entity -> clientCreateMapper.map(clientDto, entity))
                .map(clientRepository::saveAndFlush)
                .map(client -> {
                    BigDecimal balance = accountService.getClientTotalBalance(client.getId());
                    return clientReadMapper.mapFrom(client, balance);
                });
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

    public Optional<ClientResponseDto> findByPhone(String phone) {
        return clientRepository.findByPhone(phone)
                .map(client -> {
                    BigDecimal balance = accountService.getClientTotalBalance(client.getId());
                    return clientReadMapper.mapFrom(client, balance);
                });
    }

    @Transactional
    public UUID getIdFromSuccessAuth(Map<String, Object> attributes) {
        ClientRequestDto createDto = clientCreateMapper.mapToDto(attributes);
        AccountRequestDto defaultAccount = new AccountRequestDto(Currency.RUB, ACCOUNT_NAME);

        return clientRepository.findByPhone(createDto.phone())
                .map(Client::getId)
                .orElseGet(() -> {
                    UUID clientId = create(createDto).getId();
                    accountService.create(defaultAccount, clientId);
                    return clientId;
                });
    }
}
