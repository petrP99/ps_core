package com.pers.service;

import com.pers.dto.ReplenishmentCreateDto;
import com.pers.dto.ReplenishmentReadDto;
import com.pers.dto.filter.ReplenishmentFilterDto;
import com.pers.enums.Operation;
import com.pers.enums.Status;
import com.pers.mapper.ReplenishmentCreateMapper;
import com.pers.mapper.ReplenishmentReadMapper;
import com.pers.repository.CardRepository;
import com.pers.repository.ReplenishmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.pers.enums.Status.ACTIVE;
import static com.pers.enums.Status.FAILED;
import static com.pers.enums.Status.SUCCESS;
import static com.pers.util.CheckOfOperationUtil.getCardUpdateBalanceDto;


@Service
@Transactional
@RequiredArgsConstructor
public class ReplenishmentService {

    private final ReplenishmentRepository replenishmentRepository;
    private final ReplenishmentReadMapper replenishmentReadMapper;
    private final ReplenishmentCreateMapper replenishmentCreateMapper;
    private final ClientService clientService;
    private final CardService cardService;
    private final CardRepository cardRepository;

    @Transactional
    public boolean checkAndCreateReplenishment(ReplenishmentCreateDto replenishment) {
        var cardReadDto = cardService.findById(replenishment.cardId()).orElseThrow();
        UUID clientId = cardReadDto.clientId();
        Status clientStatus = clientService.findById(clientId).orElseThrow().getStatus();

        if (clientStatus == ACTIVE && cardReadDto.status() == ACTIVE) {
            create(replenishment, SUCCESS, clientId);
            var cardCreateDto = getCardUpdateBalanceDto(cardReadDto, replenishment.amount(), Operation.ADD);
            cardService.updateCardBalance(cardCreateDto);
        } else {
            create(replenishment, FAILED, clientId);
            return false;
        }
        return true;
    }

    public ReplenishmentReadDto create(ReplenishmentCreateDto replenishmentDto, Status status, UUID clientId) {
        return Optional.of(replenishmentDto)
                .map(dto -> replenishmentCreateMapper.mapFrom(dto, status, clientId))
                .map(replenishmentRepository::save)
                .map(replenishmentReadMapper::mapFrom)
                .orElseThrow();
    }

    @Transactional
    public boolean delete(Long id) {
        return replenishmentRepository.findById(id)
                .map(entity -> {
                    replenishmentRepository.delete(entity);
                    replenishmentRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    public Page<ReplenishmentReadDto> findAllByFilter(ReplenishmentFilterDto filter, Pageable pageable) {
        return replenishmentRepository.findAllByFilter(filter, pageable)
                .map(replenishmentReadMapper::mapFrom);
    }

    public Page<ReplenishmentReadDto> findByClientByFilter(ReplenishmentFilterDto filter, Pageable pageable, UUID clientId) {
        return replenishmentRepository.findAllByClientByFilter(filter, pageable, clientId)
                .map(replenishmentReadMapper::mapFrom);
    }


    public Optional<ReplenishmentReadDto> findById(Long id) {
        return replenishmentRepository.findById(id)
                .map(replenishmentReadMapper::mapFrom);
    }

    public List<ReplenishmentReadDto> findByClientId(UUID id) {
        return replenishmentRepository.findAllByClientId(id).stream()
                .map(replenishmentReadMapper::mapFrom)
                .toList();

    }
}