package com.pers.http.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class WebController {

    @GetMapping("/register")
    public void register(HttpServletResponse response) throws IOException {
        redirectToKeycloak(response, "registrations");
    }

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        redirectToKeycloak(response, "auth");
    }

    private void redirectToKeycloak(HttpServletResponse response, String action) throws IOException {
        String keycloakUrl = "http://localhost:8081/realms/psproject/protocol/openid-connect/" + action + 
                "?client_id=psproject-client&response_type=code&redirect_uri=" +
                URLEncoder.encode("http://localhost:8080", StandardCharsets.UTF_8);
        response.sendRedirect(keycloakUrl);
    }
}
