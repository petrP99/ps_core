package com.pers.service;


import com.pers.dto.filter.PaymentFilterDto;
import com.pers.dto.request.PaymentRequestDto;
import com.pers.dto.response.PaymentResponseDto;
import com.pers.entity.Account;
import com.pers.entity.Payment;
import com.pers.enums.Status;
import com.pers.exception.ErrorCode;
import com.pers.exception.PaymentException;
import com.pers.mapper.PaymentCreateMapper;
import com.pers.mapper.PaymentReadMapper;
import com.pers.repository.AccountRepository;
import com.pers.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.pers.enums.Status.SUCCESS;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentReadMapper paymentReadMapper;
    private final PaymentCreateMapper paymentCreateMapper;
    private final AccountRepository accountRepository;
    private final NotificationPublisherService notificationPublisherService;

    @Transactional
    public PaymentResponseDto pay(PaymentRequestDto payment, UUID clientId) {
        Account account = accountRepository.findByIdForUpdate(payment.accountId())
                .orElseThrow(() -> new PaymentException(
                        NOT_FOUND,
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        payment.accountId()
                ));
        if (!account.getClientId().equals(clientId)) {
            throw new PaymentException(FORBIDDEN, ErrorCode.ACCOUNT_NOT_OWNED);
        }
        if (account.getStatus() != Status.ACTIVE) {
            throw new PaymentException(CONFLICT, ErrorCode.ACCOUNT_CLOSED);
        }
        if (account.getBalance().compareTo(payment.amount()) < 0) {
            throw new PaymentException(CONFLICT, ErrorCode.ACCOUNT_INSUFFICIENT_FUNDS);
        }

        account.setBalance(account.getBalance().subtract(payment.amount()));
        accountRepository.save(account);

        Payment savedPayment = paymentRepository.save(
                paymentCreateMapper.toEntity(payment, clientId, SUCCESS)
        );
        PaymentResponseDto response = paymentReadMapper.toDto(savedPayment);
        notificationPublisherService.publish(
                clientId,
                "PAYMENT_COMPLETED",
                "Платеж выполнен",
                "Платеж на сумму " + response.amount() + " успешно выполнен",
                "ps-project",
                response.id().toString()
        );
        return response;
    }

    public Page<PaymentResponseDto> findAllByClientByFilter(PaymentFilterDto filter, Pageable pageable, UUID clientId) {
        return paymentRepository.findAllByClientByFilter(filter, pageable, clientId)
                .map(paymentReadMapper::toDto);
    }

    public Optional<PaymentResponseDto> findByIdAndClientId(UUID id, UUID clientId) {
        return paymentRepository.findByIdAndClientId(id, clientId)
                .map(paymentReadMapper::toDto);
    }

}
