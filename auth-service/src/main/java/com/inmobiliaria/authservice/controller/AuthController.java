package com.inmobiliaria.authservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @GetMapping("/health")
    public String health() {
        return "Auth Service is UP! ✅";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return "Login successful for user: " + request.getUsername();
    }

    @GetMapping("/validate")
    public String validate(@RequestParam String token) {
        return "Token is valid: " + token;
    }

    // Clase interna para el request
    static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}