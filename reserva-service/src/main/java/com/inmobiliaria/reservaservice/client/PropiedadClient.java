package com.inmobiliaria.reservaservice.client; // Paquete de clientes Feign

// ============================================================
// FEIGN CLIENT: PROPIEDAD (desde Reserva Service)
// ============================================================
// Este cliente permite que reserva-service llame a propiedad-service
// como si fuera un método Java normal, sin escribir código HTTP manual.
//
// OpenFeign (Spring Cloud):
//   1. Lee las anotaciones @GetMapping, @PatchMapping, etc.
//   2. Genera automáticamente el código HTTP necesario
//   3. Usa Eureka para encontrar la IP/puerto de propiedad-service
//   4. Hace load balancing si hay múltiples instancias
//
// Flujo en create() de ReservaService:
//   propiedadClient.findById(id)         → GET http://propiedad-service/api/v1/propiedades/{id}
//   propiedadClient.cambiarEstado(id,..) → PATCH http://propiedad-service/api/v1/propiedades/{id}/estado
// ============================================================

import lombok.Data; // Genera getters/setters para la clase interna
import org.springframework.cloud.openfeign.FeignClient; // Marca como cliente Feign
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

// @FeignClient(name = "propiedad-service"):
//   name = nombre con el que propiedad-service se registró en Eureka.
//   Feign consulta Eureka por ese nombre para encontrar la URL real.
//   Con lb:// (load balancer), distribuye entre varias instancias.
@FeignClient(name = "propiedad-service")
public interface PropiedadClient {

    // Obtiene los datos de una propiedad por su ID.
    // Mapea a: GET /api/v1/propiedades/{id} en propiedad-service.
    // @PathVariable("id"): el nombre explícito es obligatorio en interfaces Feign.
    // Retorna PropiedadDTO (no la entidad completa, solo los campos que necesitamos).
    @GetMapping("/api/v1/propiedades/{id}")
    PropiedadDTO findById(@PathVariable("id") Long id);

    // Cambia el estado de una propiedad (DISPONIBLE, ARRENDADA, INACTIVA).
    // Mapea a: PATCH /api/v1/propiedades/{id}/estado?estado=ARRENDADA en propiedad-service.
    // Se usa para marcar la propiedad como ARRENDADA al crear una reserva,
    // y como DISPONIBLE al cancelar o completar la reserva.
    @PatchMapping("/api/v1/propiedades/{id}/estado")
    PropiedadDTO cambiarEstado(@PathVariable("id") Long id, @RequestParam String estado);

    // ── DTO interno: datos de la propiedad que necesita este servicio ──
    // DTO (Data Transfer Object): clase que transporta datos entre servicios.
    // Solo incluye los campos que reserva-service realmente usa.
    // No copiamos toda la entidad Propiedad para no crear dependencias.
    @Data // Lombok: genera getters, setters, toString, equals, hashCode
    class PropiedadDTO {
        private Long id;                    // ID de la propiedad
        private String titulo;              // nombre descriptivo
        private String estado;              // "DISPONIBLE", "ARRENDADA", "INACTIVA"
        private String tipo;                // "CASA" o "DEPARTAMENTO"
        private java.math.BigDecimal precio; // precio mensual de arriendo
    }
}