package com.pers.http.rest;

import com.pers.dto.CardReadDto;
import com.pers.dto.TransferCreateDto;
import com.pers.dto.TransferReadDto;
import com.pers.dto.filter.PageResponse;
import com.pers.dto.filter.TransferFilterDto;
import com.pers.http.config.CurrentClientId;
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
import java.util.List;
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
    public ResponseEntity<List<CardReadDto>> getActiveCards(@CurrentClientId Long clientId) {
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
    public ResponseEntity<Boolean> createTransfer(@Validated @RequestBody TransferCreateDto transfer, @CurrentClientId Long clientId) {
        transfer.setClientId(clientId);
        boolean result = transferService.checkAndCreateTransfer(transfer);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my")
    public PageResponse<TransferReadDto> clientTransfers(TransferFilterDto filter, Pageable pageable, @CurrentClientId Long clientId) {
        return PageResponse.of(transferService.findAllByClientByFilter(filter, pageable, clientId));
    }

    /**
     * Methods for the Admins
     */

    // todo заменить на поиск по ид
    @GetMapping("/findAll")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public PageResponse<TransferReadDto> allTransfers(TransferFilterDto filter, Pageable pageable) {
        return PageResponse.of(transferService.findAllByFilter(filter, pageable));
    }
}
