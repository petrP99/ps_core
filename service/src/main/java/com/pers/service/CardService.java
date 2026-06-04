package com.pers.service;

import com.pers.dto.CardCreateDto;
import com.pers.dto.CardCreateDto2;
import com.pers.dto.CardReadDto;
import com.pers.dto.CardUpdateBalanceDto;
import com.pers.dto.filter.CardFilterDto;
import com.pers.entity.Client;
import com.pers.enums.Status;
import com.pers.mapper.CardCreateMapper;
import com.pers.mapper.CardReadMapper;
import com.pers.mapper.CardUpdateBalanceMapper;
import com.pers.repository.CardRepository;
import com.pers.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardReadMapper cardReadMapper;
    private final CardCreateMapper cardCreateMapper;
    private final CardUpdateBalanceMapper cardUpdateBalanceMapper;
    private final ClientRepository clientRepository;

    public Optional<CardReadDto> findById(Long id) {
        return cardRepository.findById(id)
                .map(cardReadMapper::mapFrom);
    }

    public CardReadDto create(CardCreateDto cardDto) {
        return Optional.of(cardDto)
                .map(cardCreateMapper::mapFrom)
                .map(cardRepository::save)
                .map(cardReadMapper::mapFrom)
                .orElseThrow();
    }

    public CardReadDto create(CardCreateDto2 cardDto, UUID clientId) {
        return Optional.of(cardDto)
                .map(dto -> cardCreateMapper.mapFrom(dto, clientId))
                .map(cardRepository::save)
                .map(cardReadMapper::mapFrom)
                .orElseThrow();
    }

    public Optional<CardReadDto> updateStatusToBlocked(CardReadDto cardDto) {
        return Optional.of(cardDto)
                .map(cardCreateMapper::mapStatusToBlocked)
                .map(cardRepository::saveAndFlush)
                .map(cardReadMapper::mapFrom);
    }

    public void updateStatusToExpired(CardReadDto cardDto) {
        Optional.of(cardDto)
                .map(cardCreateMapper::mapStatusExpired)
                .map(cardRepository::saveAndFlush);
    }

    public CardReadDto updateCardBalance(CardUpdateBalanceDto cardDto) {
        return Optional.of(cardDto)
                .map(cardUpdateBalanceMapper::mapFrom)
                .map(cardRepository::saveAndFlush)
                .map(cardReadMapper::mapFrom)
                .orElseThrow();
    }

    public boolean delete(Long id) {
        return cardRepository.findById(id)
                .map(entity -> {
                    cardRepository.delete(entity);
                    cardRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    public List<CardReadDto> findByClientId(UUID clientId) {
        return cardRepository.findByClientId(clientId).stream()
                .map(cardReadMapper::mapFrom)
                .toList();
    }

    public List<CardReadDto> findActiveCardsAndPositiveBalanceByClientId(UUID clientId) {
        return cardRepository.findByClientId(clientId).stream()
                .map(cardReadMapper::mapFrom)
                .filter(dto -> dto.status() == Status.ACTIVE && dto.balance().compareTo(BigDecimal.ZERO) > 0)
                .toList();
    }

    public Optional<CardReadDto> findCardByClientPhone(String phone) {
        Optional<Client> byPhone = Optional.of(clientRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Клиент по такому номер не найден")));
        return cardRepository.findByClientId(byPhone.get().getId()).stream()
                .map(cardReadMapper::mapFrom)
                .filter(card -> card.status() == Status.ACTIVE)
                .findFirst();
    }

    public Page<CardReadDto> findAllByFilter(CardFilterDto filter, Pageable pageable) {
        return cardRepository.findAllByFilter(filter, pageable)
                .map(cardReadMapper::mapFrom);
    }

    public List<CardReadDto> findActiveCardsByClientId(UUID clientId) {
        return cardRepository.findByClientId(clientId).stream()
                .map(cardReadMapper::mapFrom)
                .filter(dto -> dto.status() == Status.ACTIVE)
                .toList();
    }

    public List<CardReadDto> findByAccountId(UUID accountId) {
        return cardRepository.findByAccountId(accountId).stream()
                .map(cardReadMapper::mapFrom)
                .toList();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkCardExpire() {
        cardRepository.findAll().stream()
                .map(cardReadMapper::mapFrom)
                .filter(card -> card.status() == Status.ACTIVE && card.expireDate().isBefore(LocalDate.now()))
                .forEach(this::updateStatusToExpired);
    }
}