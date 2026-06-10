package com.pers.mapper;

import com.pers.dto.response.TransferResponseDto;
import com.pers.entity.Transfer;
import org.springframework.stereotype.Component;

@Component
public class TransferReadMapper implements Mapper<Transfer, TransferResponseDto> {
    @Override
    public TransferResponseDto toEntity(Transfer object) {
        return new TransferResponseDto(
                object.getId(),
                object.getFromClientId(),
                object.getToClientId(),
                object.getCardFrom(),
                object.getCardTo(),
                object.getAmount(),
                object.getTimeOfTransfer(),
                object.getRecipient(),
                object.getRecipientPhone(),
                object.getMessage(),
                object.getStatus(),
                object.getAmountTo(),
                object.getExchangeRate(),
                object.getCommission(),
                object.getDebitAmount(),
                object.getCurrency(),
                object.getTargetCurrency(),
                isExchange(object)
        );
    }

    private boolean isExchange(Transfer transfer) {
        return transfer.getCurrency() != null
               && transfer.getTargetCurrency() != null
               && transfer.getCurrency() != transfer.getTargetCurrency();
    }
}
