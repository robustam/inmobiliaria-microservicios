package com.inmobiliaria.busquedaservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/busqueda")
public class BusquedaController {

    @GetMapping("/health")
    public String health() {
        return "Busqueda Service is UP! ✅";
    }

    @GetMapping
    public String getBusqueda() {
        return "Servicio de busqueda";
    }
}

