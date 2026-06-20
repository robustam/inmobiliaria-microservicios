package com.inmobiliaria.busquedaservice.controller; // Paquete del controlador

// ============================================================
// CONTROLADOR HTTP: BÚSQUEDA SERVICE
// ============================================================
// Expone la búsqueda de propiedades disponibles para arriendo.
// Es un servicio de SOLO LECTURA (solo GET endpoints).
//
// Endpoints (todos bajo /api/v1/busqueda):
//   GET /health      → verificar estado del servicio
//   GET /            → búsqueda avanzada con filtros opcionales
//   GET /disponibles → todas las propiedades disponibles
//   GET /destacadas  → top 10 propiedades destacadas
//
// Todos los resultados vienen de propiedad-service via Feign.
// ============================================================

import com.inmobiliaria.busquedaservice.client.PropiedadClient;  // DTO de propiedad
import com.inmobiliaria.busquedaservice.service.BusquedaService; // Lógica de negocio
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/busqueda") // prefijo de URL
@RequiredArgsConstructor
public class BusquedaController {

    private final BusquedaService busquedaService;

    @GetMapping("/health")
    public String health() {
        return "Busqueda Service is UP! ";
    }

    // GET /api/v1/busqueda?region=Santiago&tipo=DEPARTAMENTO&precioMax=300000
    // Búsqueda avanzada con todos los filtros opcionales en la URL.
    // Parámetros:
    //   region        → región de Chile (parcial, sin importar mayúsculas)
    //   comuna        → comuna específica
    //   tipo          → "CASA" o "DEPARTAMENTO"
    //   precioMin     → precio mínimo de arriendo en CLP
    //   precioMax     → precio máximo de arriendo en CLP
    //   habitacionesMin → número mínimo de dormitorios
    //   metrosMin     → metros cuadrados mínimos
    @GetMapping
    public ResponseEntity<List<PropiedadClient.PropiedadDTO>> getBusqueda(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String comuna,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Integer habitacionesMin,
            @RequestParam(required = false) Double metrosMin) {
        return ResponseEntity.ok(
                busquedaService.buscar(region, ciudad, comuna, tipo, precioMin, precioMax, habitacionesMin, metrosMin));
    }

    // GET /api/v1/busqueda/disponibles → todas las propiedades DISPONIBLES
    @GetMapping("/disponibles")
    public ResponseEntity<List<PropiedadClient.PropiedadDTO>> getDisponibles() {
        return ResponseEntity.ok(busquedaService.getDisponibles());
    }

    // GET /api/v1/busqueda/destacadas → top 10 propiedades (para portada del sitio)
    @GetMapping("/destacadas")
    public ResponseEntity<List<PropiedadClient.PropiedadDTO>> getDestacadas() {
        return ResponseEntity.ok(busquedaService.getDestacadas());
    }
}