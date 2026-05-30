package com.pers.http.rest;

import com.pers.dto.ClientCreateDto;
import com.pers.dto.ClientReadDto;
import com.pers.dto.LoginDto;
import com.pers.service.ClientService;
import com.pers.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pers.enums.Status;
import com.pers.dto.ClientCreateDto;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakService keycloakService;
    private final ClientService clientService;

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        try {
            String token = keycloakService.getPublicClientToken(loginDto.getUsername(), loginDto.getPassword());
            return ResponseEntity.ok(Map.of("access_token", token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Неверный логин или пароль"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register() {
        return ResponseEntity.badRequest().body(Map.of("error", "Регистрация через Keycloak"));
    }
}