package com.pers.http.rest;

import com.pers.dto.request.CardRequestDto;
import com.pers.dto.response.CardResponseDto;
import com.pers.http.config.CurrentClientId;
import com.pers.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class CardRestController {

    private final CardService cardService;

    @PostMapping("/create")
    public ResponseEntity<CardResponseDto> create(@Validated @RequestBody CardRequestDto cardDto, @CurrentClientId UUID clientId) {
        CardResponseDto createdCard = cardService.create(clientId, cardDto);
        return ResponseEntity.ok(createdCard);
    }

//    @GetMapping("/{id}/block")
//    public ResponseEntity<CardResponseDto> block(@PathVariable UUID id) {
//        return cardService.findByNumber(id)
//                .flatMap(cardService::updateStatusToBlocked)
//                .map(ResponseEntity::ok)
//                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
//    }

    @GetMapping("/my")
    public List<CardResponseDto> findByClientId(@CurrentClientId UUID clientId) {
//        cardService.checkCardExpire();
        log.warn("Получен ответ по картам профиля clientId={}", clientId);
        return cardService.findByClientId(clientId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDto> findById(@PathVariable UUID id) {
        return cardService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    /**
     * Methods for the Admins
     */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!cardService.delete(id)) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/findAll")
//    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
//    public PageResponse<CardResponseDto> findAll(CardFilterDto filter, Pageable pageable) {
//        Page<CardResponseDto> page = cardService.findAllByFilter(filter, pageable);
//        return PageResponse.of(page);
//    }
}