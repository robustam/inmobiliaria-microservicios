package com.inmobiliaria.authservice.controller;

import com.inmobiliaria.authservice.dto.LoginResponse;
import com.inmobiliaria.authservice.dto.RegisterRequest;
import com.inmobiliaria.authservice.dto.TokenValidationResponse;
import com.inmobiliaria.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/health")
    public String health() {
        return "Auth Service is UP! ";
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(authService.register(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validate(@RequestParam String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }

    static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}