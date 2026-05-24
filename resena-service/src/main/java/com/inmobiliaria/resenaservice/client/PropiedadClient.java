package com.inmobiliaria.resenaservice.client; // Paquete de clientes Feign

// ============================================================
// FEIGN CLIENT: PROPIEDAD (desde Resena Service)
// ============================================================
// Permite que resena-service consulte propiedad-service para
// verificar que la propiedad existe antes de crear una reseña.
//
// Sin esta verificación, un usuario podría crear reseñas sobre
// propiedades que no existen en el sistema.
//
// Feign convierte las llamadas a métodos Java en peticiones HTTP:
//   propiedadClient.findById(id) → GET http://propiedad-service/api/v1/propiedades/{id}
// ============================================================

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient; // @FeignClient para declarar el cliente
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// @FeignClient(name = "propiedad-service"):
// Spring Cloud Feign usa Eureka para resolver "propiedad-service" a una IP:puerto real.
@FeignClient(name = "propiedad-service")
public interface PropiedadClient {

    // Obtiene datos básicos de una propiedad por ID.
    // Si la propiedad no existe, propiedad-service retorna HTTP 404,
    // y Feign lanza una excepción que GlobalExceptionHandler puede capturar.
    @GetMapping("/api/v1/propiedades/{id}")
    PropiedadDTO findById(@PathVariable("id") Long id);

    // ── DTO: datos mínimos de la propiedad que necesita resena-service ──
    @Data
    class PropiedadDTO {
        private Long id;      // ID de la propiedad
        private String titulo; // nombre descriptivo
        private String estado; // "DISPONIBLE", "ARRENDADA", "INACTIVA"
    }
}