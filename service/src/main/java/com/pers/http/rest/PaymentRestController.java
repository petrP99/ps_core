package com.pers.http.rest;

import com.pers.dto.response.CardResponseDto;
import com.pers.dto.response.PaymentResponseDto;
import com.pers.dto.request.PaymentRequestDto;
import com.pers.dto.filter.PageResponse;
import com.pers.dto.filter.PaymentFilterDto;
import com.pers.http.config.CurrentClientId;
import com.pers.service.CardService;
import com.pers.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentRestController {

    private final PaymentService paymentService;
    private final CardService cardService;

    @PostMapping("/create")
    public ResponseEntity<Boolean> create(@Validated @RequestBody PaymentRequestDto payment) {
        boolean result = paymentService.checkAndCreatePayment(payment);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my")
    public PageResponse<PaymentResponseDto> clientPayments(PaymentFilterDto filter, Pageable pageable, @CurrentClientId UUID clientId) {
        return PageResponse.of(paymentService.findAllByClientByFilter(filter, pageable, clientId));
    }

    @GetMapping("/getCards")
    public ResponseEntity<List<CardResponseDto>> getCardsForPayment(@CurrentClientId UUID clientId) {
        var cards = cardService.findByClientId(clientId);
        return ResponseEntity.ok(cards);
    }

    /**
     * Methods for the Admins
     */

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public PageResponse<PaymentResponseDto> allPayments(PaymentFilterDto filter, Pageable pageable) {
        return PageResponse.of(paymentService.findAllByFilter(filter, pageable));
    }

}