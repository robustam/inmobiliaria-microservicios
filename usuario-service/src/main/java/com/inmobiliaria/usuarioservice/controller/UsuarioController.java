package com.inmobiliaria.usuarioservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @GetMapping("/health")
    public String health() {
        return "Usuario Service is UP! ✅";
    }

    @GetMapping
    public String getUsuarios() {
        return "Lista de usuarios";
    }
}
