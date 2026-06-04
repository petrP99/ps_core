package com.pers.http.rest;

import com.pers.dto.CardReadDto;
import com.pers.dto.ClientCreateDto;
import com.pers.dto.ClientReadDto;
import com.pers.dto.filter.ClientFilterDto;
import com.pers.dto.filter.PageResponse;
import com.pers.http.config.CurrentClientId;
import com.pers.service.CardService;
import com.pers.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client")
public class ClientRestController {

    private final ClientService clientService;
    private final CardService cardService;

    @PutMapping("/update")
    public ResponseEntity<ClientReadDto> update(@CurrentClientId UUID clientId, @RequestBody @Validated ClientCreateDto client) {
        return clientService.update(clientId, client)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @GetMapping("/profile")
    public ResponseEntity<ClientReadDto> findById(@CurrentClientId UUID clientId) {
        log.warn("Получен ответ по данным профиля clientId={}", clientId);
        return clientService.findById(clientId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    // todo для вывода баланса на экран
    @GetMapping("/myBalance")
    public ResponseEntity<BigDecimal> getActiveCardsWithBalance(@CurrentClientId UUID clientId) {
        log.warn("Получен баланс профиля clientId={}", clientId);
        BigDecimal balance = cardService.findActiveCardsAndPositiveBalanceByClientId(clientId).stream()
                .map(CardReadDto::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(balance);
    }

    /**
     * Methods for the Admins
     */

    @PutMapping("/{id}/admin")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ClientReadDto> updateByAdmin(@PathVariable("id") UUID id, @RequestBody @Validated ClientCreateDto client) {
        return clientService.update(id, client)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public void delete(@PathVariable("id") UUID id) {
        if (!clientService.delete(id)) {
            throw new ResponseStatusException(NOT_FOUND);
        }
    }

    @GetMapping("/findAll")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public PageResponse<ClientReadDto> findAll(ClientFilterDto filter, Pageable pageable) {
        Page<ClientReadDto> page = clientService.findAll(filter, pageable);
        return PageResponse.of(page);
    }


}