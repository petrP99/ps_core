package com.pers.mapper;

import com.pers.dto.TransferReadDto;
import com.pers.entity.Transfer;
import org.springframework.stereotype.Component;

@Component
public class TransferReadMapper implements Mapper<Transfer, TransferReadDto> {
    @Override
    public TransferReadDto mapFrom(Transfer object) {
        return new TransferReadDto(
                object.getId(),
                object.getFromClientId(),
                object.getToClientId(),
                object.getCardIdFrom(),
                object.getCardIdTo(),
                object.getAmount(),
                object.getTimeOfTransfer(),
                object.getRecipient(),
                object.getMessage(),
                object.getStatus(),
                object.getAmountTo(),
                object.getCurrency(),
                object.getTargetCurrency(),
                isExchange(object)
        );
    }

    private boolean isExchange(Transfer transfer) {
        return transfer.getAmountTo() != null &&
               !transfer.getAmount().equals(transfer.getAmountTo());
    }
}