package com.pers.http.controller;

import com.pers.dto.request.CardRequestDto;
import com.pers.dto.response.CardResponseDto;
import com.pers.exception.CardException;
import com.pers.exception.ErrorCode;
import com.pers.http.config.ClientId;
import com.pers.service.CardService;
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

import static org.springframework.http.HttpStatus.NOT_FOUND;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class CardRestController {

    private final CardService cardService;

    @PostMapping("/create")
    public ResponseEntity<CardResponseDto> create(@Validated @RequestBody CardRequestDto cardDto, @ClientId UUID clientId) {
        CardResponseDto createdCard = cardService.create(clientId, cardDto);
        return ResponseEntity.ok(createdCard);
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<CardResponseDto> block(@PathVariable UUID id) {
        CardResponseDto card = cardService.blockById(id);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/my")
    public List<CardResponseDto> findByClientId(@ClientId UUID clientId) {
//        cardService.checkCardExpire();
        log.info("Получен ответ по картам профиля clientId={}", clientId);
        return cardService.findByClientId(clientId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDto> findById(@PathVariable UUID id) {
        return cardService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CardException(NOT_FOUND, ErrorCode.CARD_NOT_FOUND, id));
    }

}
