package com.pers.http.rest;

import com.pers.dto.ClientCreateDto;
import com.pers.dto.LoginDto;
import com.pers.entity.Client;
import com.pers.enums.Status;
import com.pers.service.ClientService;
import com.pers.service.KeycloakService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private KeycloakService keycloakService;
    @Mock
    private ClientService clientService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @Test
    void loginShouldCreateClientWhenNotExists() throws Exception {
        // Arrange
        LoginDto loginDto = new LoginDto("test@example.com", "password");
        Map<String, Object> userInfo = Map.of(
            "given_name", "John",
            "family_name", "Doe"
        );
        
        when(keycloakService.getPublicClientToken("test@example.com", "password"))
            .thenReturn("token123");
        when(keycloakService.getUserInfo("token123"))
            .thenReturn(userInfo);
        when(clientService.findByLogin("test@example.com"))
            .thenReturn(Optional.empty());
        when(clientService.create(any(ClientCreateDto.class)))
            .thenReturn(new Client());

        // Act
        ResponseEntity<Map<String, String>> response = 
            authController.login(loginDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token123", response.getBody().get("access_token"));
        verify(clientService).create(new ClientCreateDto(
            "test@example.com",
            "password",
            "John",
            "Doe",
            null,
            Status.ACTIVE,
            LocalDateTime.now()
        ));
    }

    @Test
    void loginShouldNotCreateClientWhenExists() throws Exception {
        // Arrange
        LoginDto loginDto = new LoginDto("test@example.com", "password");
        Map<String, Object> userInfo = Map.of(
            "given_name", "John",
            "family_name", "Doe"
        );
        Client existingClient = new Client();
        
        when(keycloakService.getPublicClientToken("test@example.com", "password"))
            .thenReturn("token123");
        when(keycloakService.getUserInfo("token123"))
            .thenReturn(userInfo);
        when(clientService.findByLogin("test@example.com"))
            .thenReturn(Optional.of(existingClient));

        // Act
        ResponseEntity<Map<String, String>> response = 
            authController.login(loginDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token123", response.getBody().get("access_token"));
    }

    @Test
    void loginShouldReturnUnauthorizedWhenInvalidCredentials() throws Exception {
        // Arrange
        LoginDto loginDto = new LoginDto("test@example.com", "wrongpassword");
        when(keycloakService.getPublicClientToken("test@example.com", "wrongpassword"))
            .thenThrow(new RuntimeException("Invalid credentials"));

        // Act
        ResponseEntity<Map<String, String>> response = 
            authController.login(loginDto);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Неверный логин или пароль", response.getBody().get("error"));
    }
}