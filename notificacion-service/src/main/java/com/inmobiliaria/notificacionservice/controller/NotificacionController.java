package com.inmobiliaria.notificacionservice.controller; // Paquete del controlador

// ============================================================
// CONTROLADOR HTTP: NOTIFICACIÓN SERVICE
// ============================================================
// Endpoints para gestionar notificaciones del sistema.
//
// Endpoints (todos bajo /api/v1/notificaciones):
//   GET    /health               → estado del servicio
//   GET    /?usuarioId=X         → todas las notificaciones de un usuario
//   GET    /no-leidas?usuarioId=X → notificaciones no leídas
//   GET    /resumen?usuarioId=X  → resumen (total y no leídas)
//   GET    /{id}                 → obtener notificación por ID
//   POST   /                     → crear nueva notificación
//   PATCH  /{id}/leer            → marcar una notificación como leída
//   PATCH  /leer-todas?usuarioId=X → marcar todas como leídas
//   DELETE /{id}                 → eliminar notificación
// ============================================================

import com.inmobiliaria.notificacionservice.model.Notificacion;            // Entidad
import com.inmobiliaria.notificacionservice.service.NotificacionService;   // Lógica de negocio
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping("/health")
    public String health() {
        return "Notificacion Service is UP! ✅";
    }

    // GET /api/v1/notificaciones?usuarioId=5
    // Lista todas las notificaciones de un usuario (leídas y no leídas).
    // @RequestParam Long usuarioId: extrae el parámetro de la URL (?usuarioId=5).
    @GetMapping
    public ResponseEntity<List<Notificacion>> getNotificaciones(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(notificacionService.findByUsuario(usuarioId));
    }

    // GET /api/v1/notificaciones/no-leidas?usuarioId=5
    // Solo las notificaciones pendientes (leida = false).
    // Usado para mostrar el número de alertas/notificaciones en la UI.
    @GetMapping("/no-leidas")
    public ResponseEntity<List<Notificacion>> getNoLeidas(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(notificacionService.findNoLeidasByUsuario(usuarioId));
    }

    // GET /api/v1/notificaciones/resumen?usuarioId=5
    // Retorna: { "usuarioId": 5, "total": 10, "noLeidas": 3 }
    // Útil para mostrar el badge rojo con el número de alertas.
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> getResumen(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(notificacionService.getResumenUsuario(usuarioId));
    }

    // GET /api/v1/notificaciones/{id} → notificación específica por ID
    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> getById(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.findById(id));
    }

    // POST /api/v1/notificaciones → crea nueva notificación
    // @Valid: valida @NotNull(usuarioId) y @NotBlank(titulo, mensaje)
    @PostMapping
    public ResponseEntity<Notificacion> create(@Valid @RequestBody Notificacion notificacion) {
        return ResponseEntity.status(201).body(notificacionService.create(notificacion));
    }

    // PATCH /api/v1/notificaciones/{id}/leer → marca UNA notificación como leída
    // PATCH = actualización parcial (solo cambia el campo "leida" y "leidaAt")
    @PatchMapping("/{id}/leer")
    public ResponseEntity<Notificacion> marcarLeida(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarLeida(id));
    }

    // PATCH /api/v1/notificaciones/leer-todas?usuarioId=5
    // Marca TODAS las notificaciones no leídas del usuario como leídas.
    // Retorna: { "mensaje": "Notificaciones marcadas como leídas", "cantidad": 3 }
    @PatchMapping("/leer-todas")
    public ResponseEntity<Map<String, Object>> marcarTodasLeidas(@RequestParam Long usuarioId) {
        int cantidad = notificacionService.marcarTodasLeidas(usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Notificaciones marcadas como leídas", "cantidad", cantidad));
    }

    // DELETE /api/v1/notificaciones/{id} → elimina una notificación
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        notificacionService.delete(id);
        return ResponseEntity.ok("Notificación eliminada correctamente");
    }
}