package com.inmobiliaria.propiedadservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/propiedades")
public class PropiedadController {

    @GetMapping("/health")
    public String health() {
        return "Propiedad Service is UP! ✅";
    }

    @GetMapping
    public String getPropiedades() {
        return "Lista de propiedades";
    }
}
