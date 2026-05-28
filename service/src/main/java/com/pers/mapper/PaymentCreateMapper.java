package com.pers.mapper;

import com.pers.dto.PaymentCreateDto;
import com.pers.entity.Payment;
import static com.pers.enums.Status.FAILED;
import static com.pers.enums.Status.SUCCESS;
import com.pers.repository.CardRepository;
import com.pers.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentCreateMapper implements Mapper<PaymentCreateDto, Payment> {

    private final ClientRepository clientRepository;
    private final CardRepository cardRepository;

    @Override
    public Payment mapFrom(PaymentCreateDto object) {
        return Payment.builder()
                .shopName(object.shopName())
                .amount(object.amount())
                .client(clientRepository.findById(object.clientId()).orElseThrow(IllegalArgumentException::new))
                .card(cardRepository.findById(object.cardId()).orElseThrow(IllegalArgumentException::new))
                .timeOfPay(LocalDateTime.now())
                .status(object.status() == null ? SUCCESS : FAILED)
                .build();
    }
}
