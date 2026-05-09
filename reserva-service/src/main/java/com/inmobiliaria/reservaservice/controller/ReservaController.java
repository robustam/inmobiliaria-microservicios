

package com.inmobiliaria.reservaservice.controller;

import com.inmobiliaria.reservaservice.dto.request.ReservaRequest;
import com.inmobiliaria.reservaservice.dto.response.ReservaResponse;
import com.inmobiliaria.reservaservice.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

// ¿Para que sirve el paquete controller?
// Es la puerta de entrada del microservicio
// Recibe peticiones HTTP y las delega al service
// NUNCA contiene logica de negocio
@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private static final Logger log =
            LoggerFactory.getLogger(ReservaController.class);

    private final ReservaService reservaService;

    // POST /api/v1/reservas
    // Crea una nueva solicitud de reserva
    // El estado inicial es PENDIENTE
    @PostMapping
    public ResponseEntity<ReservaResponse> crearReserva(
            @Valid @RequestBody ReservaRequest request) {

        log.info("REQUEST crear reserva → propiedad: {}",
                request.getPropiedadId());

        ReservaResponse response =
                reservaService.crearReserva(request);

        log.info("RESPONSE crear reserva → 201 CREATED");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET /api/v1/reservas/{id}
    // Obtiene una reserva por su ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> obtenerReserva(
            @PathVariable Long id) {

        log.info("REQUEST obtener reserva → id: {}", id);

        return ResponseEntity.ok(
                reservaService.obtenerReserva(id));
    }

    // GET /api/v1/reservas/arrendatario/{arrendatarioId}
    // Lista todas las reservas de un arrendatario
    // El arrendatario ve sus propias reservas
    @GetMapping("/arrendatario/{arrendatarioId}")
    public ResponseEntity<List<ReservaResponse>> listarPorArrendatario(
            @PathVariable Long arrendatarioId) {

        log.info("REQUEST listar reservas → arrendatario: {}",
                arrendatarioId);

        return ResponseEntity.ok(
                reservaService.listarPorArrendatario(
                        arrendatarioId));
    }

    // GET /api/v1/reservas/propiedad/{propiedadId}
    // Lista todas las reservas de una propiedad
    // El arrendador ve las reservas de su propiedad
    @GetMapping("/propiedad/{propiedadId}")
    public ResponseEntity<List<ReservaResponse>> listarPorPropiedad(
            @PathVariable Long propiedadId) {

        log.info("REQUEST listar reservas → propiedad: {}",
                propiedadId);

        return ResponseEntity.ok(
                reservaService.listarPorPropiedad(propiedadId));
    }

    // PUT /api/v1/reservas/{id}/aprobar
    // Aprueba una reserva pendiente
    // Solo el arrendador puede aprobar
    // Cambia estado a APROBADA y propiedad a no disponible
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<ReservaResponse> aprobarReserva(
            @PathVariable Long id) {

        log.info("REQUEST aprobar reserva → id: {}", id);

        ReservaResponse response =
                reservaService.aprobarReserva(id);

        log.info("RESPONSE aprobar reserva → 200 OK");

        return ResponseEntity.ok(response);
    }

    // PUT /api/v1/reservas/{id}/rechazar
    // Rechaza una reserva pendiente
    // Solo el arrendador puede rechazar
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<ReservaResponse> rechazarReserva(
            @PathVariable Long id) {

        log.info("REQUEST rechazar reserva → id: {}", id);

        ReservaResponse response =
                reservaService.rechazarReserva(id);

        log.info("RESPONSE rechazar reserva → 200 OK");

        return ResponseEntity.ok(response);
    }

    // PUT /api/v1/reservas/{id}/cancelar
    // Cancela una reserva pendiente o aprobada
    // El arrendatario puede cancelar su reserva
    // Si estaba aprobada libera la propiedad
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelarReserva(
            @PathVariable Long id) {

        log.info("REQUEST cancelar reserva → id: {}", id);

        ReservaResponse response =
                reservaService.cancelarReserva(id);

        log.info("RESPONSE cancelar reserva → 200 OK");

        return ResponseEntity.ok(response);
    }
}