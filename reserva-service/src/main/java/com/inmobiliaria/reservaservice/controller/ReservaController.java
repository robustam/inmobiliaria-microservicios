package com.inmobiliaria.reservaservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    @GetMapping("/health")
    public String health() {
        return "Reserva Service is UP! ✅";
    }

    @GetMapping
    public String getReservas() {
        return "Lista de reservas";
    }
}
