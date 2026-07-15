package com.pers.service;


import com.pers.dto.event.PaymentCompletedEvent;
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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentReadMapper paymentReadMapper;
    private final PaymentCreateMapper paymentCreateMapper;
    private final AccountRepository accountRepository;
    private final NotificationPublisherService notificationPublisherService;
    private final OutboxService outboxService;

    @Transactional
    public PaymentResponseDto pay(PaymentRequestDto payment, UUID clientId) {
        try {
            return executePayment(payment, clientId);
        } catch (PaymentException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            log.error(
                    "Ошибка выполнения платежа: clientId={}, accountId={}, amount={}",
                    clientId,
                    payment.accountId(),
                    payment.amount(),
                    exception
            );
            throw exception;
        }
    }

    private PaymentResponseDto executePayment(PaymentRequestDto payment, UUID clientId) {
        Account account = accountRepository.findByIdForUpdate(payment.accountId())
                .orElseThrow(() -> failPayment(
                        clientId,
                        payment.accountId(),
                        payment.amount(),
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        NOT_FOUND,
                        payment.accountId()
                ));
        if (!account.getClientId().equals(clientId)) {
            throw failPayment(
                    clientId,
                    payment.accountId(),
                    payment.amount(),
                    ErrorCode.ACCOUNT_NOT_OWNED,
                    FORBIDDEN
            );
        }
        if (account.getStatus() != Status.ACTIVE) {
            throw failPayment(
                    clientId,
                    payment.accountId(),
                    payment.amount(),
                    ErrorCode.ACCOUNT_CLOSED,
                    CONFLICT
            );
        }
        if (account.getBalance().compareTo(payment.amount()) < 0) {
            throw failPayment(
                    clientId,
                    payment.accountId(),
                    payment.amount(),
                    ErrorCode.ACCOUNT_INSUFFICIENT_FUNDS,
                    CONFLICT
            );
        }

        account.setBalance(account.getBalance().subtract(payment.amount()));
        accountRepository.save(account);

        Payment savedPayment = paymentRepository.save(
                paymentCreateMapper.toEntity(payment, clientId, SUCCESS)
        );
        outboxService.savePaymentCompleted(new PaymentCompletedEvent(
                savedPayment.getId(),
                savedPayment.getId(),
                savedPayment.getClientId(),
                savedPayment.getAccountId(),
                account.getCreatedAt(),
                savedPayment.getRecipient(),
                account.getCurrency(),
                savedPayment.getAmount(),
                savedPayment.getStatus().name(),
                savedPayment.getTimeOfPay()
        ));
        PaymentResponseDto response = paymentReadMapper.toDto(savedPayment);
        notificationPublisherService.publish(
                clientId,
                "PAYMENT_COMPLETED",
                "Платеж выполнен",
                String.format("Платеж на сумму %s успешно выполнен", response.amount()),
                "ps_core",
                response.id().toString()
        );
        log.info(
                "Платеж выполнен: paymentId={}, clientId={}, accountId={}, amount={}, currency={}, recipient={}",
                response.id(),
                response.clientId(),
                response.accountId(),
                response.amount(),
                response.currency(),
                response.recipient()
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

    private PaymentException failPayment(
            UUID clientId,
            UUID accountId,
            Object amount,
            ErrorCode errorCode,
            org.springframework.http.HttpStatus status,
            Object... arguments
    ) {
        log.warn(
                "Платеж не выполнен: clientId={}, accountId={}, amount={}, code={}",
                clientId,
                accountId,
                amount,
                errorCode
        );
        return new PaymentException(status, errorCode, arguments);
    }

}
