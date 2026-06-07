package com.pers.service;

import com.pers.dto.request.ClientRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KeycloakService {

    private final RestTemplate restTemplate;
    private final ClientService clientService;

    @Value("${keycloak.base-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public KeycloakService(ClientService clientService) {
        this.clientService = clientService;
        this.restTemplate = new RestTemplate();
    }

    public String getAdminToken() {
        String tokenUrl = keycloakBaseUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=admin-cli" +
                "&grant_type=password" +
                "&username=" + adminUsername +
                "&password=" + adminPassword;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    public String getPublicClientToken(String username, String password) {
        String tokenUrl = keycloakBaseUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&grant_type=password" +
                "&username=" + username +
                "&password=" + password;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    public boolean registerInKeycloak(ClientRequestDto dto, String rawPassword) {
        try {
            String adminToken = getAdminToken();
            String createUserUrl = keycloakBaseUrl + "/admin/realms/" + keycloakRealm + "/users";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(adminToken);

            Map<String, Object> userBody = Map.of(
                    "firstName", dto.firstName() != null ? dto.firstName() : "",
                    "lastName", dto.lastName() != null ? dto.lastName() : "",
                    "enabled", true,
                    "emailVerified", true,
                    "credentials", new Object[]{
                            Map.of("type", "password", "value", rawPassword, "temporary", false)
                    }
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(userBody, headers);
            ResponseEntity<Void> response = restTemplate.postForEntity(createUserUrl, request, Void.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String userId = getUserIdByKeycloak(adminToken, "admin"); // todo username
                if (userId != null) {
                    assignRealmRole(adminToken, userId, "USER");
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error registering user in Keycloak: {}", e.getMessage());
            return false;
        }
    }

    private String getUserIdByKeycloak(String adminToken, String username) {
        String url = keycloakBaseUrl + "/admin/realms/" + keycloakRealm + "/users?username=" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, request, List.class);

        List<Map<String, Object>> users = response.getBody();
        if (users != null && !users.isEmpty()) {
            return (String) users.get(0).get("id");
        }
        return null;
    }

    private void assignRealmRole(String adminToken, String userId, String roleName) {
        try {
            String roleUrl = keycloakBaseUrl + "/admin/realms/" + keycloakRealm + "/users/" + userId + "/role-mappings/realm";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(adminToken);

            String roleUrlGet = keycloakBaseUrl + "/admin/realms/" + keycloakRealm + "/roles/" + roleName;
            HttpEntity<Void> getRoleRequest = new HttpEntity<>(new HttpHeaders() {{
                setBearerAuth(adminToken);
            }});
            ResponseEntity<Map> roleResponse = restTemplate.getForEntity(roleUrlGet, Map.class);

            HttpEntity<List> request = new HttpEntity<>(List.of(roleResponse.getBody()), headers);
            restTemplate.postForEntity(roleUrl, request, Void.class);
        } catch (Exception e) {
            log.error("Error assigning role {}: {}", roleName, e.getMessage());
        }
    }

public Map<String, Object> getUserInfo(String accessToken) {
        String url = keycloakBaseUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get user info from Keycloak");
        }

        return response.getBody();
    }

public boolean deleteUserByUsername(String username) {
        try {
            String adminToken = getAdminToken();
            String userId = getUserIdByKeycloak(adminToken, username);
            if (userId == null) return false;

            String url = keycloakBaseUrl + "/admin/realms/" + keycloakRealm + "/users/" + userId;
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminToken);

            restTemplate.exchange(url, org.springframework.http.HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
            return true;
        } catch (Exception e) {
            log.error("Error deleting user from Keycloak: {}", e.getMessage());
            return false;
        }
    }
}