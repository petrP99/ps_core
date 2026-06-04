package com.pers.service;

import com.pers.dto.CardReadDto;
import com.pers.dto.CardUpdateBalanceDto;
import com.pers.dto.ClientReadDto;
import com.pers.dto.ClientUpdateBalanceDto;
import com.pers.dto.TransferCreateDto;
import com.pers.dto.TransferReadDto;
import com.pers.dto.filter.TransferFilterDto;
import com.pers.entity.Transfer;
import com.pers.enums.Operation;
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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static com.pers.enums.Status.FAILED;
import static com.pers.enums.Status.IN_PROGRESS;
import static com.pers.enums.Status.SUCCESS;
import static com.pers.util.CheckOfOperationUtil.calculateClientBalance;
import static com.pers.util.CheckOfOperationUtil.createClientUpdateBalanceDto;
import static com.pers.util.CheckOfOperationUtil.getCardUpdateBalanceDto;


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
    public boolean checkAndCreateTransfer(TransferCreateDto transferDto) {
        CardReadDto cardFrom = cardService.findById(transferDto.getCardIdFrom()).orElseThrow();

        if (cardFrom.status() == Status.ACTIVE && transferDto.getAmount().compareTo(cardFrom.balance()) <= 0) {
            updateClientBalance(transferDto.getCardIdFrom(), transferDto.getAmount(), Operation.SUBTRACT);
            transferDto.setStatus(IN_PROGRESS);
            Optional<Transfer> transfer = Optional.of(transferDto)
                    .map(transferCreateMapper::mapFrom)
                    .map(transferRepository::save);
            if (transfer.isPresent()) {
                transferDto.setId(transfer.get().getId());
                kafkaProducerService.sendTransferCreateEvent(transferDto);
                log.info("Перевод клиенту {} на сумму {} создан", transferDto.getRecipient(), transferDto.getAmount());
            } else {
                log.info("Ошибка при переводе клиенту {} на сумму {}.", transferDto.getRecipient(), transferDto.getAmount());
                updateClientBalance(transferDto.getCardIdFrom(), transferDto.getAmount(), Operation.ADD);
                transferDto.setStatus(FAILED);
                return false;
            }
        }
        return true;
    }

    // метод, запускающийся после чтения топика
    @Transactional
    public void completeTransfer(TransferCreateDto transfer) {
        try {
            updateClientBalance(transfer.getCardIdTo(), transfer.getAmount(), Operation.ADD);
            transfer.setStatus(SUCCESS);
            updateTransferStatus(transfer.getId(), SUCCESS);
            log.info("Перевод клиенту {} на сумму {} успешно выполнен", transfer.getRecipient(), transfer.getAmount());
        } catch (Exception e) {
            log.error("Перевод клиенту {} на сумму {} отклонен", transfer.getRecipient(), transfer.getAmount());
            updateClientBalance(transfer.getCardIdTo(), transfer.getAmount(), Operation.SUBTRACT);
            updateTransferStatus(transfer.getId(), FAILED);
        }
    }

    @Transactional
    public TransferReadDto create(TransferCreateDto transferDto) {
        return Optional.of(transferDto)
                .map(transferCreateMapper::mapFrom)
                .map(transferRepository::save)
                .map(transferReadMapper::mapFrom)
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

    public Page<TransferReadDto> findAllByClientByFilter(TransferFilterDto filter, Pageable pageable, UUID clientId) {
        return transferRepository.findAllByClientByFilter(filter, pageable, clientId)
                .map(transferReadMapper::mapFrom);
    }

    public Page<TransferReadDto> findAllByFilter(TransferFilterDto filter, Pageable pageable) {
        return transferRepository.findAllByFilter(filter, pageable)
                .map(transferReadMapper::mapFrom);
    }

    public Optional<TransferReadDto> findById(Long id) {
        return transferRepository.findById(id)
                .map(transferReadMapper::mapFrom);
    }

    public void updateTransferStatus(Long id, Status status) {
        transferRepository.findById(id)
                .ifPresent(transfer -> {
                    transfer.setStatus(status);
                    transferRepository.save(transfer);
                    log.info("Статус перевода {} обновлен на {}", id, status);
                });
    }

    private void updateClientBalance(Long cardNo, BigDecimal amount, Operation operation) {
        CardReadDto card = cardService.findById(cardNo).orElseThrow();
        ClientReadDto client = clientService.findById(card.clientId()).orElseThrow();
        CardUpdateBalanceDto cardUpdateBalanceDto =
                switch (operation) {
                    case ADD -> getCardUpdateBalanceDto(card, amount, Operation.ADD);
                    case SUBTRACT -> getCardUpdateBalanceDto(card, amount, Operation.SUBTRACT);
                };

        cardService.updateCardBalance(cardUpdateBalanceDto);
        BigDecimal clientBalance = calculateClientBalance(cardRepository.findByClientId(client.getId()), accountRepository);
        ClientUpdateBalanceDto clientFromUpdateDto = createClientUpdateBalanceDto(client, clientBalance);
        clientService.updateBalance(clientFromUpdateDto);
    }
}