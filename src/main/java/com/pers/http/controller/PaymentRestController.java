package com.pers.http.controller;

import com.pers.dto.filter.PageResponse;
import com.pers.dto.filter.PaymentFilterDto;
import com.pers.dto.request.PaymentRequestDto;
import com.pers.dto.response.PaymentResponseDto;
import com.pers.http.config.ClientId;
import com.pers.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentRestController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponseDto> create(
            @Validated @RequestBody PaymentRequestDto payment,
            @ClientId UUID clientId
    ) {
        return ResponseEntity.ok(paymentService.pay(payment, clientId));
    }

    @GetMapping("/my")
    public PageResponse<PaymentResponseDto> clientPayments(
            PaymentFilterDto filter,
            Pageable pageable,
            @ClientId UUID clientId
    ) {
        return PageResponse.of(
                paymentService.findAllByClientByFilter(filter, pageable, clientId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> getPayment(
            @PathVariable UUID id,
            @ClientId UUID clientId
    ) {
        return paymentService.findByIdAndClientId(id, clientId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
