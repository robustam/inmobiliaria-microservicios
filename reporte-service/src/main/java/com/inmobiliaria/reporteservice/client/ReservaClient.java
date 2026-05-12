
package com.inmobiliaria.reporteservice.client;

import com.inmobiliaria.reporteservice.dto.response.ReservaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// ¿Para que sirve este cliente Feign?
// Permite llamar a reserva-service para obtener
// datos de reservas y usarlos en los reportes
// Feign hace la llamada HTTP automaticamente
@FeignClient(name = "reserva-service")
public interface ReservaClient {

    // Llama a GET /api/v1/reservas/propiedad/{propiedadId}
    // del reserva-service para obtener reservas
    // de una propiedad especifica
    @GetMapping("/api/v1/reservas/propiedad/{propiedadId}")
    List<ReservaResponse> listarPorPropiedad(
            @PathVariable Long propiedadId);

    // Llama a GET /api/v1/reservas/arrendatario/{arrendatarioId}
    // del reserva-service para obtener reservas
    // de un arrendatario especifico
    @GetMapping("/api/v1/reservas/arrendatario/{arrendatarioId}")
    List<ReservaResponse> listarPorArrendatario(
            @PathVariable Long arrendatarioId);
}