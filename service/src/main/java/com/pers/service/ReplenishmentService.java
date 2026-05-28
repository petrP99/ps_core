package com.pers.service;

import com.pers.dto.ReplenishmentCreateDto;
import com.pers.dto.ReplenishmentReadDto;
import com.pers.dto.filter.ReplenishmentFilterDto;
import com.pers.enums.Operation;
import com.pers.enums.Status;
import static com.pers.enums.Status.FAILED;
import com.pers.mapper.ReplenishmentCreateMapper;
import com.pers.mapper.ReplenishmentReadMapper;
import com.pers.repository.CardRepository;
import com.pers.repository.ReplenishmentRepository;
import com.pers.util.CheckOfOperationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


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
        var clientReadDto = clientService.findById(replenishment.clientId()).orElseThrow();
        var cardReadDto = cardService.findById(replenishment.cardId()).orElseThrow();

        if (clientReadDto.getStatus() == Status.ACTIVE && cardReadDto.status() == Status.ACTIVE) {
            create(replenishment);
            var cardCreateDto = CheckOfOperationUtil.getCardUpdateBalanceDto(cardReadDto, replenishment.amount(), Operation.ADD);
            cardService.updateCardBalance(cardCreateDto);
            var newBalance = CheckOfOperationUtil.calculateClientBalance(cardRepository.findByClientId(replenishment.clientId()));
            var clientUpdateBalanceDto = CheckOfOperationUtil.createClientUpdateBalanceDto(clientReadDto, newBalance);
            clientService.updateBalance(clientUpdateBalanceDto);
        } else {
            var replenishmentFail = new ReplenishmentCreateDto(
                    replenishment.clientId(),
                    replenishment.cardId(),
                    replenishment.amount(),
                    replenishment.timeOfReplenishment(),
                    FAILED);
            create(replenishmentFail);
            return false;
        }
        return true;
    }

    @Transactional
    public ReplenishmentReadDto create(ReplenishmentCreateDto replenishmentDto) {
        return Optional.of(replenishmentDto)
                .map(replenishmentCreateMapper::mapFrom)
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

    public Page<ReplenishmentReadDto> findByClientByFilter(ReplenishmentFilterDto filter, Pageable pageable, Long clientId) {
        return replenishmentRepository.findAllByClientByFilter(filter, pageable, clientId)
                .map(replenishmentReadMapper::mapFrom);
    }


    public Optional<ReplenishmentReadDto> findById(Long id) {
        return replenishmentRepository.findById(id)
                .map(replenishmentReadMapper::mapFrom);
    }

    public List<ReplenishmentReadDto> findByClientId(Long id) {
        return replenishmentRepository.findAllByClientToId(id).stream()
                .map(replenishmentReadMapper::mapFrom)
                .toList();

    }
}