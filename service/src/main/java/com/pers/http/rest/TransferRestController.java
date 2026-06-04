package com.pers.http.rest;

import com.pers.dto.CardReadDto;
import com.pers.dto.TransferCreateDto;
import com.pers.dto.TransferReadDto;
import com.pers.dto.filter.PageResponse;
import com.pers.dto.filter.TransferFilterDto;
import com.pers.http.config.CurrentClientId;
import com.pers.service.CardService;
import com.pers.service.TransferService;
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
@RequestMapping("/api/v1/transfers")
public class TransferRestController {

    private final TransferService transferService;
    private final CardService cardService;

    @GetMapping("/cards")
    public ResponseEntity<List<CardReadDto>> getActiveCards(@CurrentClientId UUID clientId) {
        List<CardReadDto> cards = cardService.findActiveCardsByClientId(clientId);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/preview")
    public ResponseEntity<TransferCreateDto> previewTransfer(@Validated @RequestBody TransferCreateDto transfer) {
        return ResponseEntity.ok(transfer);
    }

    @PostMapping("/preview-phone")
    public ResponseEntity<TransferCreateDto> previewPhoneTransfer(@Validated @RequestBody TransferCreateDto transfer) {
        return ResponseEntity.ok(transfer);
    }

    @PostMapping("/create")
    public ResponseEntity<Boolean> createTransfer(@Validated @RequestBody TransferCreateDto transfer, @CurrentClientId UUID clientId) {
        transfer.setFromClientId(clientId);
        boolean result = transferService.checkAndCreateTransfer(transfer);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my")
    public PageResponse<TransferReadDto> clientTransfers(TransferFilterDto filter, Pageable pageable, @CurrentClientId UUID clientId) {
        return PageResponse.of(transferService.findAllByClientByFilter(filter, pageable, clientId));
    }

    /**
     * Methods for the Admins
     */

    @GetMapping("/findAll")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public PageResponse<TransferReadDto> allTransfers(TransferFilterDto filter, Pageable pageable) {
        return PageResponse.of(transferService.findAllByFilter(filter, pageable));
    }
}