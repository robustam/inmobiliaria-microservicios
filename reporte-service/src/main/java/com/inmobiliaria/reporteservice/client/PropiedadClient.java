
package com.inmobiliaria.reporteservice.client;

import com.inmobiliaria.reporteservice.dto.response.PropiedadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

// ¿Para que sirve este cliente Feign?
// Permite llamar a propiedad-service para obtener
// datos de propiedades y usarlos en los reportes
// Feign hace la llamada HTTP automaticamente
@FeignClient(name = "propiedad-service")
public interface PropiedadClient {

    // Llama a GET /api/v1/propiedades
    // del propiedad-service para obtener
    // todas las propiedades del sistema
    @GetMapping("/api/v1/propiedades")
    List<PropiedadResponse> listarPropiedades();

    // Llama a GET /api/v1/propiedades/disponibles
    // del propiedad-service para obtener
    // solo las propiedades disponibles
    @GetMapping("/api/v1/propiedades/disponibles")
    List<PropiedadResponse> listarDisponibles();
}