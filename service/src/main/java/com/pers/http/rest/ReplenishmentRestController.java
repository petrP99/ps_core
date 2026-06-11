package com.pers.http.rest;

import com.pers.dto.request.ReplenishmentRequestDto;
import com.pers.dto.response.ReplenishmentResponseDto;
import com.pers.http.config.CurrentClientId;
import com.pers.service.ReplenishmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/replenishments")
public class ReplenishmentRestController {

    private final ReplenishmentService replenishmentService;

    @PostMapping
    public ResponseEntity<ReplenishmentResponseDto> replenish(
            @Validated @RequestBody ReplenishmentRequestDto request,
            @CurrentClientId UUID clientId
    ) {
        return ResponseEntity.ok(replenishmentService.replenish(request, clientId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReplenishmentResponseDto>> getMyReplenishments(
            @CurrentClientId UUID clientId
    ) {
        return ResponseEntity.ok(replenishmentService.findByClientId(clientId));
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<List<ReplenishmentResponseDto>> getByAccount(
            @PathVariable UUID accountId,
            @CurrentClientId UUID clientId
    ) {
        return ResponseEntity.ok(replenishmentService.findByAccountId(accountId, clientId));
    }
}
