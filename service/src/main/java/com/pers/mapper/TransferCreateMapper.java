package com.pers.mapper;

import com.pers.dto.TransferCreateDto;
import static com.pers.enums.Status.FAILED;
import static com.pers.enums.Status.SUCCESS;
import com.pers.entity.Transfer;
import com.pers.repository.CardRepository;
import com.pers.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TransferCreateMapper implements Mapper<TransferCreateDto, Transfer> {

    private final CardRepository cardRepository;
    private final ClientRepository clientRepository;

    @Override
    public Transfer mapFrom(TransferCreateDto dto) {
        var card = cardRepository.findById(dto.getCardIdTo()).orElseThrow();
        var recipientClientId = card.getClient().getId();
        var recipient = clientRepository.findFirstAndLastNameByClientId(recipientClientId);

        return Transfer.builder()
                .clientId(clientRepository.findById(dto.getClientId()).orElseThrow(IllegalArgumentException::new))
                .cardNoFrom(cardRepository.findById(dto.getCardIdFrom()).orElseThrow(IllegalArgumentException::new))
                .cardNoTo(cardRepository.findById(dto.getCardIdTo()).orElseThrow(IllegalArgumentException::new))
                .amount(dto.getAmount())
                .recipient(recipient)
                .status(dto.getStatus())
                .message(dto.getMessage())
                .timeOfTransfer(LocalDateTime.now())
                .build();
    }
}
