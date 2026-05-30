package com.pers.http.rest;

import com.pers.dto.CardReadDto;
import com.pers.dto.TransferCreateDto;
import com.pers.dto.TransferReadDto;
import com.pers.dto.filter.PageResponse;
import com.pers.dto.filter.TransferFilterDto;
import com.pers.service.CardService;
import com.pers.service.ClientService;
import com.pers.service.TransferService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.web.mappings.MappingsEndpoint;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfers")
public class TransferRestController {

    private final TransferService transferService;
    private final CardService cardService;
    private final ClientService clientService;

    @GetMapping("/cards")
    public ResponseEntity<?> getActiveCards(HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Клиент не авторизован"));
        }
        var cards = cardService.findActiveCardsByClientId(clientId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/cards/balance")
    public ResponseEntity<BigDecimal> getActiveCardsWithBalance(HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
//        if (clientId == null) {
//            return ResponseEntity.badRequest().body(Map.of("error", "Клиент не авторизован"));
//        }
        BigDecimal balance = cardService.findActiveCardsAndPositiveBalanceByClientId(clientId).stream()
                .map(CardReadDto::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/preview")
    public ResponseEntity<Map<String, Object>> previewTransfer(@Validated @RequestBody TransferCreateDto transfer, HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Клиент не авторизован"));
        }
        // Logic for preview - find recipient etc.
        // For simplicity, call service if it has preview, or simulate
        Map<String, Object> preview = Map.of("valid", true); // Implement preview logic
        return ResponseEntity.ok(preview);
    }

    @PostMapping("/preview-phone")
    public ResponseEntity<Map<String, Object>> previewPhoneTransfer(@RequestParam String phone, @Validated @RequestBody TransferCreateDto transfer, HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Клиент не авторизован"));
        }
        // Logic similar to check-phone
        Map<String, Object> preview = Map.of("valid", true); // Implement
        return ResponseEntity.ok(preview);
    }

    @PostMapping
    public ResponseEntity<?> createTransfer(@Validated @RequestBody TransferCreateDto transfer, HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Клиент не авторизован"));
        }
        transfer.setClientId(clientId);
        boolean success = transferService.checkAndCreateTransfer(transfer);
        return ResponseEntity.ok(Map.of("status", success ? "success" : "fail"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!transferService.delete(id)) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client")
    public PageResponse<TransferReadDto> clientTransfers(TransferFilterDto filter, Pageable pageable, HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        return PageResponse.of(transferService.findAllByClientByFilter(filter, pageable, clientId));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public PageResponse<TransferReadDto> allTransfers(TransferFilterDto filter, Pageable pageable) {
        return PageResponse.of(transferService.findAllByFilter(filter, pageable));
    }
}
