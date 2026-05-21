package com.inmobiliaria.notificacionservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {

    @GetMapping("/health")
    public String health() {
        return "Notificacion Service is UP! ✅";
    }

    @GetMapping
    public String getNotificaciones() {
        return "Lista de notificaciones";
    }
}
