package com.pers.http.rest;

import com.pers.dto.ReplenishmentCreateDto;
import com.pers.dto.ReplenishmentReadDto;
import com.pers.dto.filter.PageResponse;
import com.pers.dto.filter.ReplenishmentFilterDto;
import com.pers.service.CardService;
import com.pers.service.ReplenishmentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/replenishments")
public class ReplenishmentRestController {

    private final ReplenishmentService replenishmentService;
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<?> create(@Validated @RequestBody ReplenishmentCreateDto replenishment, HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Клиент не авторизован"));
        }
        boolean success = replenishmentService.checkAndCreateReplenishment(replenishment);
        return ResponseEntity.ok(Map.of("status", success ? "success" : "fail"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!replenishmentService.delete(id)) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public PageResponse<ReplenishmentReadDto> allReplenishments(ReplenishmentFilterDto filter, Pageable pageable) {
        return PageResponse.of(replenishmentService.findAllByFilter(filter, pageable));
    }

    @GetMapping("/client")
    public PageResponse<ReplenishmentReadDto> clientReplenishments(ReplenishmentFilterDto filter, Pageable pageable, HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        return PageResponse.of(replenishmentService.findByClientByFilter(filter, pageable, clientId));
    }

    @GetMapping("/cards")
    public ResponseEntity<?> getCardsForReplenishment(HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Клиент не авторизован"));
        }
        var cards = cardService.findActiveCardsByClientId(clientId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/cards/{cardId}")
    public ResponseEntity<?> getCardForReplenishment(@PathVariable Long cardId, HttpSession session) {
        Long clientId = (Long) session.getAttribute("clientId");
        if (clientId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Клиент не авторизован"));
        }
        // Assuming cardService has findById or similar
        return cardService.findById(cardId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
