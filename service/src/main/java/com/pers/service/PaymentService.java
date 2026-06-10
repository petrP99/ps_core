package com.pers.service;


import com.pers.dto.filter.PaymentFilterDto;
import com.pers.dto.request.PaymentRequestDto;
import com.pers.dto.response.PaymentResponseDto;
import com.pers.entity.Account;
import com.pers.enums.AccountStatus;
import com.pers.mapper.PaymentCreateMapper;
import com.pers.mapper.PaymentReadMapper;
import com.pers.repository.AccountRepository;
import com.pers.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.pers.enums.Status.SUCCESS;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentReadMapper paymentReadMapper;
    private final PaymentCreateMapper paymentCreateMapper;
    private final AccountRepository accountRepository;

    @Transactional
    public PaymentResponseDto pay(PaymentRequestDto payment, UUID clientId) {
        Account account = accountRepository.findByIdForUpdate(payment.accountId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Счет не найден"
                ));
        if (!account.getClientId().equals(clientId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Счет не принадлежит текущему клиенту"
            );
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Счет закрыт");
        }
        if (account.getBalance().compareTo(payment.amount()) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Недостаточно средств на счете"
            );
        }

        account.setBalance(account.getBalance().subtract(payment.amount()));
        accountRepository.save(account);

        return Optional.of(payment)
                .map(dto -> paymentCreateMapper.mapFrom(dto, clientId, SUCCESS))
                .map(paymentRepository::save)
                .map(paymentReadMapper::toDto)
                .orElseThrow();
    }

    @Transactional
    public boolean delete(UUID id) {
        return paymentRepository.findById(id)
                .map(entity -> {
                    paymentRepository.delete(entity);
                    paymentRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    public Page<PaymentResponseDto> findAllByFilter(PaymentFilterDto filter, Pageable pageable) {
        return paymentRepository.findAllByFilter(filter, pageable)
                .map(paymentReadMapper::toDto);
    }

    public Page<PaymentResponseDto> findAllByClientByFilter(PaymentFilterDto filter, Pageable pageable, UUID clientId) {
        return paymentRepository.findAllByClientByFilter(filter, pageable, clientId)
                .map(paymentReadMapper::toDto);
    }

    public Optional<PaymentResponseDto> findById(UUID id) {
        return paymentRepository.findById(id)
                .map(paymentReadMapper::toDto);
    }

    public Optional<PaymentResponseDto> findByIdAndClientId(UUID id, UUID clientId) {
        return paymentRepository.findByIdAndClientId(id, clientId)
                .map(paymentReadMapper::toDto);
    }

    public List<PaymentResponseDto> findByClientId(UUID clientId) {
        return paymentRepository.findAllByClientIdOrderByTimeOfPayDesc(clientId).stream()
                .map(paymentReadMapper::toDto)
                .toList();
    }

}
