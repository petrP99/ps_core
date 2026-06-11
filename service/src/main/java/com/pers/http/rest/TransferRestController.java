package com.pers.http.rest;

import com.pers.dto.filter.PageResponse;
import com.pers.dto.filter.TransferFilterDto;
import com.pers.dto.request.PhoneTransferPreviewRequestDto;
import com.pers.dto.request.PhoneTransferRequestDto;
import com.pers.dto.request.TransferPreviewRequestDto;
import com.pers.dto.request.TransferRequestDto;
import com.pers.dto.response.CardResponseDto;
import com.pers.dto.response.TransferHistoryResponseDto;
import com.pers.dto.response.TransferPreviewResponseDto;
import com.pers.dto.response.TransferResponseDto;
import com.pers.exception.TransferException;
import com.pers.exception.ErrorCode;
import com.pers.http.config.CurrentClientId;
import com.pers.service.CardService;
import com.pers.service.TransferService;
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

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

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
    public ResponseEntity<TransferPreviewResponseDto> previewTransfer(
            @Validated @RequestBody TransferPreviewRequestDto transfer,
            @CurrentClientId UUID clientId
    ) {
        return ResponseEntity.ok(transferService.previewTransfer(transfer, clientId));
    }

    @PostMapping("/preview-phone")
    public ResponseEntity<TransferPreviewResponseDto> previewPhoneTransfer(
            @Validated @RequestBody PhoneTransferPreviewRequestDto transfer,
            @CurrentClientId UUID clientId
    ) {
        return ResponseEntity.ok(transferService.previewPhoneTransfer(transfer, clientId));
    }

    @PostMapping("/create")
    public ResponseEntity<TransferResponseDto> createTransfer(
            @Validated @RequestBody TransferRequestDto transfer,
            @CurrentClientId UUID clientId
    ) {
        transfer.setFromClientId(clientId);
        return ResponseEntity.ok(transferService.checkAndCreateTransfer(transfer));
    }

    @PostMapping("/create-phone")
    public ResponseEntity<TransferResponseDto> createPhoneTransfer(
            @Validated @RequestBody PhoneTransferRequestDto transfer,
            @CurrentClientId UUID clientId
    ) {
        return ResponseEntity.ok(transferService.checkAndCreatePhoneTransfer(transfer, clientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferResponseDto> getTransfer(
            @PathVariable UUID id,
            @CurrentClientId UUID clientId
    ) {
        return transferService.findByIdAndClientId(id, clientId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new TransferException(
                        NOT_FOUND,
                        ErrorCode.TRANSFER_NOT_FOUND,
                        id
                ));
    }

    @GetMapping("/my")
    public PageResponse<TransferHistoryResponseDto> clientTransfers(
            Pageable pageable,
            @CurrentClientId UUID clientId
    ) {
        return PageResponse.of(transferService.findHistoryByClient(pageable, clientId));
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<TransferHistoryResponseDto> getTransferHistoryDetails(
            @PathVariable UUID id,
            @CurrentClientId UUID clientId
    ) {
        return transferService.findHistoryByIdAndClientId(id, clientId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new TransferException(
                        NOT_FOUND,
                        ErrorCode.TRANSFER_NOT_FOUND,
                        id
                ));
    }

}
