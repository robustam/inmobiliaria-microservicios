package com.inmobiliaria.imagenservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/imagenes")
public class ImagenController {

    @GetMapping("/health")
    public String health() {
        return "Imagen Service is UP! ✅";
    }

    @GetMapping
    public String getImagenes() {
        return "Lista de imagenes";
    }
}
