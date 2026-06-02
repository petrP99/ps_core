package com.pers.http.rest;

import com.pers.dto.CardReadDto;
import com.pers.dto.ReplenishmentCreateDto;
import com.pers.dto.ReplenishmentReadDto;
import com.pers.dto.filter.PageResponse;
import com.pers.dto.filter.ReplenishmentFilterDto;
import com.pers.http.config.CurrentClientId;
import com.pers.service.CardService;
import com.pers.service.ReplenishmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/replenishments")
public class ReplenishmentRestController {

    private final ReplenishmentService replenishmentService;
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<Boolean> create(@Validated @RequestBody ReplenishmentCreateDto replenishment) {
        boolean result = replenishmentService.checkAndCreateReplenishment(replenishment);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my")
    public PageResponse<ReplenishmentReadDto> clientReplenishments(ReplenishmentFilterDto filter, Pageable pageable, @CurrentClientId Long clientId) {
        return PageResponse.of(replenishmentService.findByClientByFilter(filter, pageable, clientId));
    }

    @GetMapping("/cards")
    public ResponseEntity<List<CardReadDto>> getCardsForReplenishment(@CurrentClientId Long clientId) {
        var cards = cardService.findActiveCardsByClientId(clientId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/cards/{cardId}")
    public ResponseEntity<?> getCardForReplenishment(@PathVariable Long cardId) {
        return cardService.findById(cardId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Methods for the Admins
     */

    // todo заменить на поиск по ид
    @GetMapping("/findAll")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public PageResponse<ReplenishmentReadDto> allReplenishments(ReplenishmentFilterDto filter, Pageable pageable) {
        return PageResponse.of(replenishmentService.findAllByFilter(filter, pageable));
    }

}
