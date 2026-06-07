package com.pers.service;

import com.pers.dto.response.TransferResponseDto;
import com.pers.dto.request.TransferRequestDto;
import com.pers.dto.filter.TransferFilterDto;
import com.pers.enums.Status;
import com.pers.kafka.KafkaProducerService;
import com.pers.mapper.TransferCreateMapper;
import com.pers.mapper.TransferReadMapper;
import com.pers.repository.AccountRepository;
import com.pers.repository.CardRepository;
import com.pers.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final TransferRepository transferRepository;
    private final TransferReadMapper transferReadMapper;
    private final TransferCreateMapper transferCreateMapper;
    private final ClientService clientService;
    private final CardService cardService;
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final KafkaProducerService kafkaProducerService;

    // метода запускающийся при создании перевода
    @Transactional
    public boolean checkAndCreateTransfer(TransferRequestDto transferDto) {
//        CardReadDto cardFrom = cardService.findByNumber(transferDto.getCardIdFrom()).orElseThrow();
//
//        if (cardFrom.status() == Status.ACTIVE && transferDto.getAmount().compareTo(cardFrom.balance()) <= 0) {
//            updateClientBalance(transferDto.getCardIdFrom(), transferDto.getAmount(), Operation.SUBTRACT);
//            transferDto.setStatus(IN_PROGRESS);
//            Optional<Transfer> transfer = Optional.of(transferDto)
//                    .map(transferCreateMapper::toDto)
//                    .map(transferRepository::save);
//            if (transfer.isPresent()) {
//                transferDto.setId(transfer.get().getId());
//                kafkaProducerService.sendTransferCreateEvent(transferDto);
//                log.info("Перевод клиенту {} на сумму {} создан", transferDto.getRecipient(), transferDto.getAmount());
//            } else {
//                log.info("Ошибка при переводе клиенту {} на сумму {}.", transferDto.getRecipient(), transferDto.getAmount());
//                updateClientBalance(transferDto.getCardIdFrom(), transferDto.getAmount(), Operation.ADD);
//                transferDto.setStatus(FAILED);
//                return false;
//            }
//        }
        return true;
    }
//
//    // метод, запускающийся после чтения топика
    @Transactional
    public void completeTransfer(TransferRequestDto transfer) {
//        try {
//            updateClientBalance(transfer.getCardIdTo(), transfer.getAmount(), Operation.ADD);
//            transfer.setStatus(SUCCESS);
//            updateTransferStatus(transfer.getId(), SUCCESS);
//            log.info("Перевод клиенту {} на сумму {} успешно выполнен", transfer.getRecipient(), transfer.getAmount());
//        } catch (Exception e) {
//            log.error("Перевод клиенту {} на сумму {} отклонен", transfer.getRecipient(), transfer.getAmount());
//            updateClientBalance(transfer.getCardIdTo(), transfer.getAmount(), Operation.SUBTRACT);
//            updateTransferStatus(transfer.getId(), FAILED);
//        }
    }

    @Transactional
    public TransferResponseDto create(TransferRequestDto transferDto) {
        return Optional.of(transferDto)
                .map(transferCreateMapper::toEntity)
                .map(transferRepository::save)
                .map(transferReadMapper::toEntity)
                .orElseThrow();
    }

    @Transactional
    public boolean delete(Long id) {
        return transferRepository.findById(id)
                .map(entity -> {
                    transferRepository.delete(entity);
                    transferRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    public Page<TransferResponseDto> findAllByClientByFilter(TransferFilterDto filter, Pageable pageable, UUID clientId) {
        return transferRepository.findAllByClientByFilter(filter, pageable, clientId)
                .map(transferReadMapper::toEntity);
    }

    public Page<TransferResponseDto> findAllByFilter(TransferFilterDto filter, Pageable pageable) {
        return transferRepository.findAllByFilter(filter, pageable)
                .map(transferReadMapper::toEntity);
    }

    public Optional<TransferResponseDto> findById(Long id) {
        return transferRepository.findById(id)
                .map(transferReadMapper::toEntity);
    }

    public void updateTransferStatus(Long id, Status status) {
        transferRepository.findById(id)
                .ifPresent(transfer -> {
                    transfer.setStatus(status);
                    transferRepository.save(transfer);
                    log.info("Статус перевода {} обновлен на {}", id, status);
                });
    }

}