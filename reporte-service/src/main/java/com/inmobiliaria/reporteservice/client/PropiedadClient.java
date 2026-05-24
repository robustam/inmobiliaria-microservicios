package com.inmobiliaria.reporteservice.client; // Paquete de clientes Feign

// ============================================================
// FEIGN CLIENT: PROPIEDAD (desde Reporte Service)
// ============================================================
// Permite que reporte-service obtenga TODAS las propiedades
// (incluyendo INACTIVAS) para generar estadísticas completas.
//
// Feign: GET http://propiedad-service/api/v1/propiedades/todas
// ============================================================

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;
import java.util.List;

// @FeignClient: Feign usa Eureka para resolver "propiedad-service" a su IP:puerto real.
@FeignClient(name = "propiedad-service")
public interface PropiedadClient {

    // Obtiene TODAS las propiedades (no solo las DISPONIBLES).
    // Necesario para reportes: queremos contar DISPONIBLES, ARRENDADAS e INACTIVAS.
    // Mapea a: GET /api/v1/propiedades/todas en propiedad-service.
    @GetMapping("/api/v1/propiedades/todas")
    List<PropiedadDTO> findAll();

    // ── DTO: campos necesarios para las estadísticas de propiedades ──
    @Data
    class PropiedadDTO {
        private Long id;              // ID único
        private String titulo;        // nombre de la propiedad
        private BigDecimal precio;    // precio mensual (para calcular ingresos potenciales)
        private String ciudad;        // ciudad (para agrupar por ciudad)
        private String tipo;          // "CASA" o "DEPARTAMENTO" (para agrupar por tipo)
        private String estado;        // "DISPONIBLE", "ARRENDADA", "INACTIVA" (para agrupar)
        private Long propietarioId;   // propietario (para reportes por propietario)
    }
}