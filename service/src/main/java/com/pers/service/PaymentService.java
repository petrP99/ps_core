package com.pers.service;


import com.pers.dto.filter.PaymentFilterDto;
import com.pers.dto.request.PaymentRequestDto;
import com.pers.dto.response.PaymentResponseDto;
import com.pers.enums.Status;
import com.pers.mapper.PaymentCreateMapper;
import com.pers.mapper.PaymentReadMapper;
import com.pers.repository.AccountRepository;
import com.pers.repository.CardRepository;
import com.pers.repository.PaymentRepository;
import com.pers.util.CheckOfOperationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.pers.enums.Status.FAILED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentReadMapper paymentReadMapper;
    private final PaymentCreateMapper paymentCreateMapper;
    private final ClientService clientService;
    private final CardService cardService;
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public boolean checkAndCreatePayment(PaymentRequestDto payment) {
        var clientReadDto = clientService.findById(payment.clientId()).orElseThrow();
        var cardReadDto = cardService.findByNumber(payment.cardNo()).orElseThrow();
        var amount = payment.amount();

        if (clientReadDto.getStatus() == Status.ACTIVE
            && cardReadDto.status() == Status.ACTIVE
            && amount.compareTo(cardReadDto.balance()) <= 0) {

            create(payment);

            var newBalance = CheckOfOperationUtil.calculateClientBalance(cardRepository.findByClientId(payment.clientId()), accountRepository);

        } else {
            var paymentFail = new PaymentRequestDto(
                    payment.shopName(),
                    payment.amount(),
                    payment.clientId(),
                    payment.cardNo(),
                    payment.timeOfPay(),
                    FAILED);
            create(paymentFail);
            return false;
        }
        return true;
    }

    @Transactional
    public PaymentResponseDto create(PaymentRequestDto paymentDto) {
        return Optional.of(paymentDto)
                .map(paymentCreateMapper::toEntity)
                .map(paymentRepository::save)
                .map(paymentReadMapper::toDto)
                .orElseThrow();
    }

    @Transactional
    public boolean delete(Long id) {
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

    public Optional<PaymentResponseDto> findById(Long id) {
        return paymentRepository.findById(id)
                .map(paymentReadMapper::toDto);
    }

}