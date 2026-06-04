package com.pers.http.rest;

import com.pers.dto.CardCreateDto;
import com.pers.dto.CardCreateDto2;
import com.pers.dto.CardReadDto;
import com.pers.dto.filter.CardFilterDto;
import com.pers.dto.filter.PageResponse;
import com.pers.http.config.CurrentClientId;
import com.pers.service.CardService;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class CardRestController {

    private final CardService cardService;

    @PostMapping("/create")
    public ResponseEntity<CardReadDto> create(@Validated @RequestBody CardCreateDto2 card, @CurrentClientId UUID clientId) {
        CardReadDto createdCard = cardService.create(card, clientId);
        return ResponseEntity.ok(createdCard);
    }

    @GetMapping("/{id}/block")
    public ResponseEntity<CardReadDto> block(@PathVariable Long id) {
        return cardService.findById(id)
                .flatMap(cardService::updateStatusToBlocked)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @GetMapping("/my")
    public List<CardReadDto> findByClientId(@CurrentClientId UUID clientId) {
        cardService.checkCardExpire();
        log.warn("Получен ответ по картам профиля clientId={}", clientId);
        return cardService.findByClientId(clientId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardReadDto> findById(@PathVariable Long id) {
        return cardService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    /**
     * Methods for the Admins
     */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!cardService.delete(id)) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/findAll")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public PageResponse<CardReadDto> findAll(CardFilterDto filter, Pageable pageable) {
        Page<CardReadDto> page = cardService.findAllByFilter(filter, pageable);
        return PageResponse.of(page);
    }
}