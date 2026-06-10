package com.pers.service;

import com.pers.dto.filter.ReplenishmentFilterDto;
import com.pers.dto.request.ReplenishmentRequestDto;
import com.pers.dto.response.ReplenishmentResponseDto;
import com.pers.entity.Account;
import com.pers.enums.AccountStatus;
import com.pers.mapper.ReplenishmentCreateMapper;
import com.pers.mapper.ReplenishmentReadMapper;
import com.pers.repository.AccountRepository;
import com.pers.repository.ReplenishmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.pers.enums.Status.SUCCESS;


@Service
@Transactional
@RequiredArgsConstructor
public class ReplenishmentService {

    private final ReplenishmentRepository replenishmentRepository;
    private final ReplenishmentReadMapper replenishmentReadMapper;
    private final ReplenishmentCreateMapper replenishmentCreateMapper;
    private final AccountRepository accountRepository;

    @Transactional
    public ReplenishmentResponseDto replenish(
            ReplenishmentRequestDto replenishment,
            UUID clientId
    ) {
        Account account = accountRepository.findByIdForUpdate(replenishment.accountId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Счет не найден"
                ));
        if (!account.getClientId().equals(clientId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Счет не принадлежит текущему клиенту"
            );
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT,
                    "Счет закрыт"
            );
        }

        account.setBalance(account.getBalance().add(replenishment.amount()));
        return create(replenishment, clientId);
    }

    private ReplenishmentResponseDto create(
            ReplenishmentRequestDto replenishmentDto,
            UUID clientId
    ) {
        return Optional.of(replenishmentDto)
                .map(dto -> replenishmentCreateMapper.mapFrom(dto, SUCCESS, clientId))
                .map(replenishmentRepository::save)
                .map(replenishmentReadMapper::toEntity)
                .orElseThrow();
    }

    @Transactional
    public boolean delete(UUID id) {
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


    public Optional<ReplenishmentResponseDto> findById(UUID id) {
        return replenishmentRepository.findById(id)
                .map(replenishmentReadMapper::toEntity);
    }

    public List<ReplenishmentResponseDto> findByClientId(UUID id) {
        return replenishmentRepository.findAllByClientIdOrderByTimeOfReplenishmentDesc(id).stream()
                .map(replenishmentReadMapper::toEntity)
                .toList();
    }

    public List<ReplenishmentResponseDto> findByAccountId(UUID accountId, UUID clientId) {
        return replenishmentRepository
                .findAllByAccountIdAndClientIdOrderByTimeOfReplenishmentDesc(accountId, clientId)
                .stream()
                .map(replenishmentReadMapper::toEntity)
                .toList();
    }
}
