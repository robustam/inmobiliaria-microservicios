
package com.inmobiliaria.busquedaservice.client;

import com.inmobiliaria.busquedaservice.dto.response.PropiedadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

// ¿Para que sirve este paquete client?
// Permite llamar a propiedad-service como si fuera
// un metodo normal de Java
// Feign hace la llamada HTTP automaticamente
// Sin Feign tendriamos que escribir mucho codigo HTTP

// @FeignClient conecta con propiedad-service
// name debe ser EXACTAMENTE igual al
// spring.application.name del propiedad-service
@FeignClient(name = "propiedad-service")
public interface PropiedadClient {

    // Llama a GET /api/v1/propiedades
    // del propiedad-service para obtener todas las propiedades
    // Feign convierte la respuesta JSON a List<PropiedadResponse>
    // automaticamente
    @GetMapping("/api/v1/propiedades")
    List<PropiedadResponse> listarPropiedades();

    // Llama a GET /api/v1/propiedades/disponibles
    // del propiedad-service para obtener solo disponibles
    @GetMapping("/api/v1/propiedades/disponibles")
    List<PropiedadResponse> listarDisponibles();
}