package com.pers.http.controller;

import com.pers.dto.response.ClientResponseDto;
import com.pers.exception.BusinessException;
import com.pers.exception.ErrorCode;
import com.pers.http.config.ClientId;
import com.pers.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client")
public class ClientRestController {

    private final ClientService clientService;

    @GetMapping("/profile")
    public ResponseEntity<ClientResponseDto> findById(@ClientId UUID clientId) {
        log.info("Получен ответ по данным профиля clientId={}", clientId);
        return clientService.findById(clientId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BusinessException(
                        NOT_FOUND,
                        ErrorCode.CLIENT_NOT_FOUND,
                        clientId
                ));
    }
}
