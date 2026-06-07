package com.pers.mapper;

import com.pers.dto.request.TransferRequestDto;
import com.pers.entity.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TransferCreateMapper implements Mapper<TransferRequestDto, Transfer> {

    @Override
    public Transfer toEntity(TransferRequestDto dto) {
        return Transfer.builder()
                .fromClientId(dto.getFromClientId())
                .toClientId(dto.getToClientId())
                .cardFrom(dto.getCardFrom())
                .cardTo(dto.getCardTo())
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