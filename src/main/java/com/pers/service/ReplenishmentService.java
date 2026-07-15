package com.pers.service;

import com.pers.dto.request.ReplenishmentRequestDto;
import com.pers.dto.response.ReplenishmentResponseDto;
import com.pers.entity.Account;
import com.pers.entity.Replenishment;
import com.pers.enums.Status;
import com.pers.exception.ErrorCode;
import com.pers.exception.ReplenishmentException;
import com.pers.mapper.ReplenishmentCreateMapper;
import com.pers.mapper.ReplenishmentReadMapper;
import com.pers.repository.AccountRepository;
import com.pers.repository.ReplenishmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.pers.enums.Status.SUCCESS;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@Service
@Transactional
@RequiredArgsConstructor
public class ReplenishmentService {

    private final ReplenishmentRepository replenishmentRepository;
    private final ReplenishmentReadMapper replenishmentReadMapper;
    private final ReplenishmentCreateMapper replenishmentCreateMapper;
    private final AccountRepository accountRepository;
    private final NotificationPublisherService notificationPublisherService;

    @Transactional
    public ReplenishmentResponseDto replenish(ReplenishmentRequestDto replenishment, UUID clientId) {
        Account account = accountRepository.findByIdForUpdate(replenishment.accountId())
                .orElseThrow(() -> new ReplenishmentException(
                        NOT_FOUND,
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        replenishment.accountId()
                ));
        if (!account.getClientId().equals(clientId)) {
            throw new ReplenishmentException(FORBIDDEN, ErrorCode.ACCOUNT_NOT_OWNED);
        }
        if (account.getStatus() != Status.ACTIVE) {
            throw new ReplenishmentException(CONFLICT, ErrorCode.ACCOUNT_CLOSED);
        }

        account.setBalance(account.getBalance().add(replenishment.amount()));
        return create(replenishment, clientId);
    }

    private ReplenishmentResponseDto create(ReplenishmentRequestDto replenishmentDto, UUID clientId) {
        Replenishment savedReplenishment = replenishmentRepository.save(
                replenishmentCreateMapper.mapFrom(replenishmentDto, SUCCESS, clientId)
        );
        ReplenishmentResponseDto response = replenishmentReadMapper.toEntity(savedReplenishment);
        notificationPublisherService.publish(
                clientId,
                "ACCOUNT_REPLENISHED",
                "Счет пополнен",
                "Пополнение на сумму " + response.amount() + " успешно выполнено",
                "ps_core",
                response.id().toString()
        );
        return response;
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
