package com.pers.http.rest;

import com.pers.dto.response.CardResponseDto;
import com.pers.dto.response.TransferResponseDto;
import com.pers.dto.request.TransferRequestDto;
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
    public ResponseEntity<List<CardResponseDto>> getActiveCards(@CurrentClientId UUID clientId) {
        List<CardResponseDto> cards = cardService.findByClientId(clientId);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/preview")
    public ResponseEntity<TransferRequestDto> previewTransfer(@Validated @RequestBody TransferRequestDto transfer) {
        return ResponseEntity.ok(transfer);
    }

    @PostMapping("/preview-phone")
    public ResponseEntity<TransferRequestDto> previewPhoneTransfer(@Validated @RequestBody TransferRequestDto transfer) {
        return ResponseEntity.ok(transfer);
    }

    @PostMapping("/create")
    public ResponseEntity<Boolean> createTransfer(@Validated @RequestBody TransferRequestDto transfer, @CurrentClientId UUID clientId) {
        transfer.setFromClientId(clientId);
        boolean result = transferService.checkAndCreateTransfer(transfer);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my")
    public PageResponse<TransferResponseDto> clientTransfers(TransferFilterDto filter, Pageable pageable, @CurrentClientId UUID clientId) {
        return PageResponse.of(transferService.findAllByClientByFilter(filter, pageable, clientId));
    }

    /**
     * Methods for the Admins
     */

    @GetMapping("/findAll")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public PageResponse<TransferResponseDto> allTransfers(TransferFilterDto filter, Pageable pageable) {
        return PageResponse.of(transferService.findAllByFilter(filter, pageable));
    }
}