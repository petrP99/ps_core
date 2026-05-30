package com.pers.http.rest;

import com.pers.dto.PaymentCreateDto;
import com.pers.dto.PaymentReadDto;
import com.pers.dto.filter.PageResponse;
import com.pers.dto.filter.PaymentFilterDto;
import com.pers.service.CardService;
import com.pers.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentRestController {

    private final PaymentService paymentService;
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<?> create(@Validated @RequestBody PaymentCreateDto payment, HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Клиент не авторизован"));
        }
        boolean success = paymentService.checkAndCreatePayment(payment);
        return ResponseEntity.ok(Map.of("status", success ? "success" : "fail"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!paymentService.delete(id)) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client")
    public PageResponse<PaymentReadDto> clientPayments(PaymentFilterDto filter, Pageable pageable, HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        return PageResponse.of(paymentService.findAllByClientByFilter(filter, pageable, clientId));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public PageResponse<PaymentReadDto> allPayments(PaymentFilterDto filter, Pageable pageable) {
        return PageResponse.of(paymentService.findAllByFilter(filter, pageable));
    }

    @GetMapping("/cards")
    public ResponseEntity<?> getCardsForPayment(HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Клиент не авторизован"));
        }
        var cards = cardService.findActiveCardsAndPositiveBalanceByClientId(clientId);
        return ResponseEntity.ok(cards);
    }
}
