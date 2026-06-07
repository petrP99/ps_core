package com.pers.service;

import com.pers.dto.filter.ReplenishmentFilterDto;
import com.pers.dto.request.ReplenishmentRequestDto;
import com.pers.dto.response.ReplenishmentResponseDto;
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
    public boolean checkAndCreateReplenishment(ReplenishmentRequestDto replenishment) {
        var cardReadDto = cardService.findByNumber(replenishment.cardNo()).orElseThrow();
        UUID clientId = cardReadDto.clientId();
        Status clientStatus = clientService.findById(clientId).orElseThrow().getStatus();

        if (clientStatus == ACTIVE && cardReadDto.status() == ACTIVE) {
            create(replenishment, SUCCESS, clientId);
        } else {
            create(replenishment, FAILED, clientId);
            return false;
        }
        return true;
    }

    public ReplenishmentResponseDto create(ReplenishmentRequestDto replenishmentDto, Status status, UUID clientId) {
        return Optional.of(replenishmentDto)
                .map(dto -> replenishmentCreateMapper.mapFrom(dto, status, clientId))
                .map(replenishmentRepository::save)
                .map(replenishmentReadMapper::toEntity)
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

    public Page<ReplenishmentResponseDto> findAllByFilter(ReplenishmentFilterDto filter, Pageable pageable) {
        return replenishmentRepository.findAllByFilter(filter, pageable)
                .map(replenishmentReadMapper::toEntity);
    }

    public Page<ReplenishmentResponseDto> findByClientByFilter(ReplenishmentFilterDto filter, Pageable pageable, UUID clientId) {
        return replenishmentRepository.findAllByClientByFilter(filter, pageable, clientId)
                .map(replenishmentReadMapper::toEntity);
    }


    public Optional<ReplenishmentResponseDto> findById(Long id) {
        return replenishmentRepository.findById(id)
                .map(replenishmentReadMapper::toEntity);
    }

    public List<ReplenishmentResponseDto> findByClientId(UUID id) {
        return replenishmentRepository.findAllByClientId(id).stream()
                .map(replenishmentReadMapper::toEntity)
                .toList();

    }
}