package com.inmobiliaria.reservaservice.controller; // Paquete del controlador

// ============================================================
// CONTROLADOR HTTP: RESERVA SERVICE
// ============================================================
// Endpoints para gestionar reservas de arriendo.
//
// Endpoints (todos bajo /api/v1/reservas):
//   GET    /health                  → verificar estado del servicio
//   GET    /                        → listar todas las reservas
//   GET    /{id}                    → obtener reserva por ID
//   GET    /usuario/{usuarioId}     → reservas de un usuario
//   GET    /propiedad/{propiedadId} → reservas de una propiedad
//   POST   /                        → crear nueva reserva
//   PATCH  /{id}/estado?estado=...  → cambiar estado de la reserva
//   DELETE /{id}                    → eliminar reserva
// ============================================================

import com.inmobiliaria.reservaservice.model.Reserva;          // Entidad
import com.inmobiliaria.reservaservice.service.ReservaService; // Lógica de negocio
import jakarta.validation.Valid;           // Activa validaciones (@NotNull, @Positive)
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas") // prefijo de URL para todos los endpoints
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    // GET /api/v1/reservas/health → verificación de vida del servicio
    @GetMapping("/health")
    public String health() {
        return "Reserva Service is UP! ✅";
    }

    // GET /api/v1/reservas → lista todas las reservas (admin)
    @GetMapping
    public ResponseEntity<List<Reserva>> getReservas() {
        return ResponseEntity.ok(reservaService.findAll());
    }

    // GET /api/v1/reservas/{id} → obtiene una reserva por ID
    // HTTP 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.findById(id));
    }

    // GET /api/v1/reservas/usuario/{usuarioId} → reservas de un arrendatario
    // El usuario puede ver sus propias reservas
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Reserva>> getByUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(reservaService.findByUsuario(usuarioId));
    }

    // GET /api/v1/reservas/propiedad/{propiedadId} → historial de una propiedad
    // El propietario puede ver quién ha arrendado su propiedad
    @GetMapping("/propiedad/{propiedadId}")
    public ResponseEntity<List<Reserva>> getByPropiedad(@PathVariable Long propiedadId) {
        return ResponseEntity.ok(reservaService.findByPropiedad(propiedadId));
    }

    // POST /api/v1/reservas → crea una nueva reserva
    // @Valid: valida @NotNull (propiedadId, usuarioId, fechaInicio, fechaFin)
    //        y @Positive (monto)
    // HTTP 201 Created al crear exitosamente
    // También llama a propiedad-service para verificar disponibilidad y cambiar estado
    @PostMapping
    public ResponseEntity<Reserva> create(@Valid @RequestBody Reserva reserva) {
        return ResponseEntity.status(201).body(reservaService.create(reserva));
    }

    // PATCH /api/v1/reservas/{id}/estado?estado=CONFIRMADA
    // Cambia el estado de la reserva.
    // Al cancelar o completar, libera automáticamente la propiedad.
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Reserva> cambiarEstado(@PathVariable Long id,
                                                  @RequestParam String estado) {
        return ResponseEntity.ok(reservaService.cambiarEstado(id, estado));
    }

    // DELETE /api/v1/reservas/{id} → elimina físicamente la reserva y libera la propiedad
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        reservaService.delete(id);
        return ResponseEntity.ok("Reserva eliminada correctamente");
    }
}