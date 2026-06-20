package com.inmobiliaria.busquedaservice.client; // Paquete de clientes Feign

// ============================================================
// FEIGN CLIENT: PROPIEDAD (desde Busqueda Service)
// ============================================================
// Busqueda Service NO tiene base de datos propia.
// Funciona como un PROXY/AGREGADOR que consulta propiedad-service
// y aplica filtros adicionales (habitaciones, metros cuadrados).
//
// Este cliente Feign hace las peticiones HTTP a propiedad-service:
//   buscar()        → GET /api/v1/propiedades/buscar?region=...&tipo=...
//   findDisponibles() → GET /api/v1/propiedades
//
// Feign usa Eureka para encontrar propiedad-service sin hardcodear URLs.
// ============================================================

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;
import java.util.List;

// @FeignClient(name = "propiedad-service"): usa el nombre Eureka para encontrar el servicio.
@FeignClient(name = "propiedad-service")
public interface PropiedadClient {

    // Llama al endpoint de búsqueda avanzada de propiedad-service.
    // Los parámetros opcionales se pasan como query params en la URL.
    // Ejemplo: GET /api/v1/propiedades/buscar?region=Santiago&tipo=DEPARTAMENTO
    @GetMapping("/api/v1/propiedades/buscar")
    List<PropiedadDTO> buscar(
            @RequestParam(required = false) String region,     // filtro por región
            @RequestParam(required = false) String ciudad,     // filtro por ciudad
            @RequestParam(required = false) String comuna,     // filtro por comuna
            @RequestParam(required = false) String tipo,       // "CASA" o "DEPARTAMENTO"
            @RequestParam(required = false) BigDecimal precioMin, // precio mínimo
            @RequestParam(required = false) BigDecimal precioMax); // precio máximo

    // Obtiene todas las propiedades DISPONIBLES (sin filtros).
    // GET /api/v1/propiedades
    @GetMapping("/api/v1/propiedades")
    List<PropiedadDTO> findDisponibles();

    // ── DTO con todos los datos de propiedad que busqueda-service necesita ──
    // Incluye habitaciones y metrosCuadrados para los filtros adicionales
    // que aplica busqueda-service (no disponibles en la consulta de propiedad-service).
    @Data // Lombok: genera getters y setters automáticamente
    class PropiedadDTO {
        private Long id;                   // ID único
        private String titulo;             // nombre descriptivo
        private String descripcion;        // descripción completa
        private BigDecimal precio;         // precio mensual
        private String moneda;             // "CLP" o "UF"
        private String region;             // región de Chile
        private String ciudad;             // ciudad
        private String comuna;             // comuna
        private String direccion;          // dirección (puede ser null)
        private Integer habitaciones;      // número de dormitorios
        private Integer banos;             // número de baños
        private Double metrosCuadrados;    // superficie en m²
        private String tipo;               // "CASA" o "DEPARTAMENTO"
        private String estado;             // "DISPONIBLE" (solo estas llegan aquí)
        private Long propietarioId;        // ID del dueño
    }
}