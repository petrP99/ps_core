package com.pers.http.rest;

import com.pers.dto.AccountCreateDto;
import com.pers.dto.AccountReadDto;
import com.pers.http.config.CurrentClientId;
import com.pers.service.AccountService;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountRestController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<AccountReadDto> create(@Validated @RequestBody AccountCreateDto dto,
                                                  @CurrentClientId UUID clientId) {
        AccountReadDto account = accountService.create(dto, clientId);
        log.warn("Создан новый счет clientId={}, accountId={}", clientId, account.id());
        return ResponseEntity.ok(account);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<AccountReadDto> getById(@PathVariable("id") UUID id) {
        return accountService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }
}