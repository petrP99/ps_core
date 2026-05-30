package com.pers.service;

import com.pers.dto.ClientCreateDto;
import com.pers.dto.ClientReadDto;
import com.pers.dto.ClientUpdateBalanceDto;
import com.pers.dto.filter.ClientFilterDto;
import com.pers.entity.Client;
import com.pers.mapper.ClientCreateMapper;
import com.pers.mapper.ClientReadMapper;
import com.pers.mapper.ClientUpdateBalanceMapper;
import com.pers.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientReadMapper clientReadMapper;
    private final ClientCreateMapper clientCreateMapper;
    private final ClientUpdateBalanceMapper clientUpdateBalanceMapper;

    public Page<ClientReadDto> findAll(ClientFilterDto filter, Pageable pageable) {
        return clientRepository.findAllByFilter(filter, pageable)
                .map(clientReadMapper::mapFrom);
    }

    public String findFirstAndLastNameByClientId(Long id) {
        return clientRepository.findFirstAndLastNameByClientId(id);
    }

    public Optional<ClientReadDto> findById(Long id) {
        return clientRepository.findById(id)
                .map(clientReadMapper::mapFrom);
    }

    @Transactional
    public ClientReadDto create(ClientCreateDto clientDto) {
        return Optional.of(clientDto)
                .map(clientCreateMapper::mapFrom)
                .map(clientRepository::save)
                .map(clientReadMapper::mapFrom)
                .orElseThrow();
    }

    @Transactional
    public Optional<ClientReadDto> update(Long id, ClientCreateDto clientDto) {
        return clientRepository.findById(id)
                .map(entity -> clientCreateMapper.map(clientDto, entity))
                .map(clientRepository::saveAndFlush)
                .map(clientReadMapper::mapFrom);
    }

    @Transactional
    public ClientReadDto updateBalance(ClientUpdateBalanceDto clientDto) {
        return Optional.of(clientDto)
                .map(clientUpdateBalanceMapper::mapFrom)
                .map(clientRepository::saveAndFlush)
                .map(clientReadMapper::mapFrom)
                .orElseThrow();
    }

    @Transactional
    public boolean delete(Long id) {
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
                .map(clientReadMapper::mapFrom);
    }

    public Optional<Client> findByPhoneEntity(String phone) {
        return clientRepository.findByPhone(phone);
    }

    @Transactional
    public Long getIdFromSuccessAuth(Map<String, Object> attributes) {
        ClientCreateDto createDto = clientCreateMapper.mapToDto(attributes);

        return clientRepository.findByPhone(createDto.phone())
                .map(Client::getId)
                .orElseGet(() -> create(createDto).getId());
    }
}