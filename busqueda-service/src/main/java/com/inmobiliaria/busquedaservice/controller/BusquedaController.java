
package com.inmobiliaria.busquedaservice.controller;

import com.inmobiliaria.busquedaservice.dto.request.BusquedaRequest;
import com.inmobiliaria.busquedaservice.dto.response.PropiedadResponse;
import com.inmobiliaria.busquedaservice.service.BusquedaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

// ¿Para que sirve el paquete controller?
// Es la puerta de entrada del microservicio
// Recibe peticiones HTTP y las delega al service
// NUNCA contiene logica de negocio
@RestController
@RequestMapping("/api/v1/busqueda")
@RequiredArgsConstructor
public class BusquedaController {

    private static final Logger log =
            LoggerFactory.getLogger(BusquedaController.class);

    private final BusquedaService busquedaService;

    // POST /api/v1/busqueda/propiedades
    // Busca propiedades aplicando filtros opcionales
    // El cliente envia los filtros en el body JSON
    // Ejemplo body:
    // {
    //   "comuna": "Providencia",
    //   "precioMax": 500000,
    //   "habitaciones": 2,
    //   "soloDisponibles": true
    // }
    @PostMapping("/propiedades")
    public ResponseEntity<List<PropiedadResponse>> buscar(
            // @RequestBody convierte el JSON a BusquedaRequest
            // No usamos @Valid porque todos los campos
            // son opcionales en la busqueda
            @RequestBody BusquedaRequest request) {

        log.info("REQUEST buscar propiedades → filtros: {}",
                request);

        List<PropiedadResponse> response =
                busquedaService.buscar(request);

        log.info("RESPONSE buscar → encontradas: {}",
                response.size());

        return ResponseEntity.ok(response);
    }

    // GET /api/v1/busqueda/disponibles
    // Lista todas las propiedades disponibles
    // sin necesidad de enviar filtros
    @GetMapping("/disponibles")
    public ResponseEntity<List<PropiedadResponse>> listarDisponibles() {

        log.info("REQUEST listar disponibles");

        List<PropiedadResponse> response =
                busquedaService.listarDisponibles();

        log.info("RESPONSE disponibles → total: {}",
                response.size());

        return ResponseEntity.ok(response);
    }
}
