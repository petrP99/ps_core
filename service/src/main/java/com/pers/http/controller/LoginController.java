package com.pers.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/old")
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    // todo @GetMapping("/clients/home/")
    // @PreAuthorize("hasAuthority('USER')")
    public String homePage() {
        return "client/home";
    }

    @GetMapping("/admin/main")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public String main() {
        return "admin/main";
    }
}
