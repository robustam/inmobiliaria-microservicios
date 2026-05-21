package com.inmobiliaria.reporteservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    @GetMapping("/health")
    public String health() {
        return "Reporte Service is UP! ✅";
    }

    @GetMapping
    public String getReportes() {
        return "Lista de reportes";
    }
}
