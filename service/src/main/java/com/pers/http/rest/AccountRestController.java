package com.pers.http.rest;

import com.pers.dto.request.AccountRequestDto;
import com.pers.dto.response.AccountResponseDto;
import com.pers.exception.AccountException;
import com.pers.exception.ErrorCode;
import com.pers.http.config.CurrentClientId;
import com.pers.service.AccountClosureService;
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

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountRestController {

    private final AccountService accountService;
    private final AccountClosureService accountClosureService;

    @PostMapping("/create")
    public ResponseEntity<AccountResponseDto> create(@Validated @RequestBody AccountRequestDto dto,
                                                     @CurrentClientId UUID clientId) {
        AccountResponseDto account = accountService.create(dto, clientId);
        log.info("Создан новый счет clientId={}, accountId={}", clientId, account.id());
        return ResponseEntity.ok(account);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<AccountResponseDto> getById(@PathVariable("id") UUID id) {
        return accountService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new AccountException(NOT_FOUND, ErrorCode.ACCOUNT_NOT_FOUND, id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<AccountResponseDto>> getAll(@CurrentClientId UUID clientId) {
        List<AccountResponseDto> all = accountService.findAll(clientId);
        return ResponseEntity.ok(all);
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<Void> close(@PathVariable UUID id, @CurrentClientId UUID clientId) {
        accountClosureService.requestClosure(id, clientId);
        return ResponseEntity.status(ACCEPTED).build();
    }
}
