package com.inmobiliaria.resenaservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resenas")
public class ResenaController {

    @GetMapping("/health")
    public String health() {
        return "Reseña Service is UP! ✅";
    }

    @GetMapping
    public String getResenas() {
        return "Lista de reseñas";
    }
}