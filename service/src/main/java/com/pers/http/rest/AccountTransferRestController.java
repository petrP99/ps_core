package com.pers.http.rest;

import com.pers.dto.request.AccountTransferRequestDto;
import com.pers.dto.response.AccountTransferResponseDto;
import com.pers.http.config.CurrentClientId;
import com.pers.service.AccountTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account-transfers")
public class AccountTransferRestController {

    private final AccountTransferService accountTransferService;

    @PostMapping("/preview")
    public ResponseEntity<AccountTransferResponseDto> preview(
            @Valid @RequestBody AccountTransferRequestDto request,
            @CurrentClientId UUID clientId) {
        return ResponseEntity.ok(accountTransferService.preview(request, clientId));
    }

    @PostMapping
    public ResponseEntity<AccountTransferResponseDto> transfer(
            @Valid @RequestBody AccountTransferRequestDto request,
            @CurrentClientId UUID clientId) {
        return ResponseEntity.ok(accountTransferService.transfer(request, clientId));
    }
}
