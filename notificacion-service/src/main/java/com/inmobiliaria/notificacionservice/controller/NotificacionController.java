
package com.inmobiliaria.notificacionservice.controller;

import com.inmobiliaria.notificacionservice.dto.request.NotificacionRequest;
import com.inmobiliaria.notificacionservice.dto.response.NotificacionResponse;
import com.inmobiliaria.notificacionservice.service.NotificacionService;
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
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private static final Logger log =
            LoggerFactory.getLogger(NotificacionController.class);

    private final NotificacionService notificacionService;

    // POST /api/v1/notificaciones
    // Crea una nueva notificacion
    // Lo usan otros microservicios internamente
    // Ejemplo: reserva-service notifica cuando aprueba
    @PostMapping
    public ResponseEntity<NotificacionResponse> crearNotificacion(
            @Valid @RequestBody NotificacionRequest request) {

        log.info("REQUEST crear notificacion → usuario: {}",
                request.getUsuarioId());

        NotificacionResponse response =
                notificacionService.crearNotificacion(request);

        log.info("RESPONSE crear notificacion → 201 CREATED");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET /api/v1/notificaciones/usuario/{usuarioId}
    // Lista todas las notificaciones de un usuario
    // Ordenadas de mas reciente a mas antigua
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponse>> listarPorUsuario(
            @PathVariable Long usuarioId) {

        log.info("REQUEST listar notificaciones → usuario: {}",
                usuarioId);

        return ResponseEntity.ok(
                notificacionService.listarPorUsuario(usuarioId));
    }

    // GET /api/v1/notificaciones/usuario/{usuarioId}/sin-leer
    // Lista solo notificaciones sin leer de un usuario
    @GetMapping("/usuario/{usuarioId}/sin-leer")
    public ResponseEntity<List<NotificacionResponse>> listarSinLeer(
            @PathVariable Long usuarioId) {

        log.info("REQUEST listar sin leer → usuario: {}",
                usuarioId);

        return ResponseEntity.ok(
                notificacionService.listarSinLeer(usuarioId));
    }

    // GET /api/v1/notificaciones/usuario/{usuarioId}/contar
    // Cuenta notificaciones sin leer de un usuario
    // Util para mostrar el numero en el icono de campana
    @GetMapping("/usuario/{usuarioId}/contar")
    public ResponseEntity<Long> contarSinLeer(
            @PathVariable Long usuarioId) {

        log.info("REQUEST contar sin leer → usuario: {}",
                usuarioId);

        return ResponseEntity.ok(
                notificacionService.contarSinLeer(usuarioId));
    }

    // PATCH /api/v1/notificaciones/{id}/leer
    // Marca una notificacion especifica como leida
    // PATCH se usa para actualizar un solo campo
    @PatchMapping("/{id}/leer")
    public ResponseEntity<NotificacionResponse> marcarComoLeida(
            @PathVariable Long id) {

        log.info("REQUEST marcar como leida → id: {}", id);

        return ResponseEntity.ok(
                notificacionService.marcarComoLeida(id));
    }

    // PATCH /api/v1/notificaciones/usuario/{usuarioId}/leer-todas
    // Marca TODAS las notificaciones de un usuario como leidas
    @PatchMapping("/usuario/{usuarioId}/leer-todas")
    public ResponseEntity<String> marcarTodasComoLeidas(
            @PathVariable Long usuarioId) {

        log.info("REQUEST marcar todas como leidas → usuario: {}",
                usuarioId);

        notificacionService.marcarTodasComoLeidas(usuarioId);

        return ResponseEntity.ok(
                "Todas las notificaciones marcadas como leidas");
    }
}