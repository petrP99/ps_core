package com.pers.mapper;

import com.pers.dto.TransferCreateDto;
import com.pers.entity.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TransferCreateMapper implements Mapper<TransferCreateDto, Transfer> {

    @Override
    public Transfer mapFrom(TransferCreateDto dto) {
        return Transfer.builder()
                .fromClientId(dto.getFromClientId())
                .toClientId(dto.getToClientId())
                .cardIdFrom(dto.getCardIdFrom())
                .cardIdTo(dto.getCardIdTo())
                .amount(dto.getAmount())
                .recipient(dto.getRecipient())
                .status(dto.getStatus())
                .message(dto.getMessage())
                .timeOfTransfer(LocalDateTime.now())
                .amountTo(dto.getAmountTo())
                .currency(dto.getCurrency())
                .targetCurrency(dto.getTargetCurrency())
                .build();
    }
}