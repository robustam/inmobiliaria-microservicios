

package com.inmobiliaria.reservaservice.client;

import com.inmobiliaria.reservaservice.dto.response.PropiedadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

// ¿Para que sirve el paquete client?
// Permite llamar a otros microservicios como si fueran
// metodos normales de Java — Feign hace la llamada HTTP
// automaticamente sin que tengamos que escribir el codigo HTTP

// @FeignClient conecta este servicio con propiedad-service
// name debe ser EXACTAMENTE igual al spring.application.name
// del microservicio al que queremos llamar
@FeignClient(name = "propiedad-service")
public interface PropiedadClient {

    // Llama a GET /api/v1/propiedades/{id}
    // del propiedad-service para obtener datos de la propiedad
    // Feign genera automaticamente el codigo HTTP:
    // GET http://propiedad-service/api/v1/propiedades/{id}
    @GetMapping("/api/v1/propiedades/{id}")
    PropiedadResponse getPropiedadById(@PathVariable Long id);

    // Llama a PATCH /api/v1/propiedades/{id}/disponibilidad
    // del propiedad-service para cambiar disponibilidad
    // Cuando se aprueba una reserva la propiedad pasa a
    // no disponible — cuando se cancela vuelve a disponible
    @PatchMapping("/api/v1/propiedades/{id}/disponibilidad")
    PropiedadResponse cambiarDisponibilidad(
            @PathVariable Long id,
            @RequestParam Boolean valor);
}
