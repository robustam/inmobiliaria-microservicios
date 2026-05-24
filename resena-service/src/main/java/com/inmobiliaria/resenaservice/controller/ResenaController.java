package com.inmobiliaria.resenaservice.controller; // Paquete del controlador

// ============================================================
// CONTROLADOR HTTP: RESEÑA SERVICE
// ============================================================
// Endpoints para gestionar reseñas y evaluaciones de propiedades.
//
// Endpoints (todos bajo /api/v1/resenas):
//   GET    /health                              → estado del servicio
//   GET    /                                    → listar todas las reseñas
//   GET    /{id}                                → obtener reseña por ID
//   GET    /propiedad/{propiedadId}             → reseñas de una propiedad
//   GET    /propiedad/{propiedadId}/estadisticas → promedio y total de una propiedad
//   GET    /usuario/{usuarioId}                 → reseñas escritas por un usuario
//   POST   /                                    → crear nueva reseña
//   PUT    /{id}                                → actualizar reseña
//   DELETE /{id}                                → eliminar reseña
// ============================================================

import com.inmobiliaria.resenaservice.model.Resena;          // Entidad
import com.inmobiliaria.resenaservice.service.ResenaService; // Lógica de negocio
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resenas") // prefijo de URL para todos los endpoints
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @GetMapping("/health")
    public String health() {
        return "Reseña Service is UP! ✅";
    }

    // GET /api/v1/resenas → lista todas las reseñas
    @GetMapping
    public ResponseEntity<List<Resena>> getResenas() {
        return ResponseEntity.ok(resenaService.findAll());
    }

    // GET /api/v1/resenas/{id} → obtiene una reseña por ID
    @GetMapping("/{id}")
    public ResponseEntity<Resena> getById(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.findById(id));
    }

    // GET /api/v1/resenas/propiedad/{propiedadId} → reseñas de una propiedad
    // Los arrendatarios ven las evaluaciones de una propiedad antes de reservar
    @GetMapping("/propiedad/{propiedadId}")
    public ResponseEntity<List<Resena>> getByPropiedad(@PathVariable Long propiedadId) {
        return ResponseEntity.ok(resenaService.findByPropiedad(propiedadId));
    }

    // GET /api/v1/resenas/propiedad/{propiedadId}/estadisticas
    // Retorna: { "propiedadId": 1, "totalResenas": 5, "promedioCalificacion": 4.2 }
    @GetMapping("/propiedad/{propiedadId}/estadisticas")
    public ResponseEntity<Map<String, Object>> getEstadisticas(@PathVariable Long propiedadId) {
        return ResponseEntity.ok(resenaService.getEstadisticasPropiedad(propiedadId));
    }

    // GET /api/v1/resenas/usuario/{usuarioId} → reseñas escritas por un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Resena>> getByUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(resenaService.findByUsuario(usuarioId));
    }

    // POST /api/v1/resenas → crea una nueva reseña
    // @Valid: valida @NotNull (propiedadId, usuarioId, calificacion) y @Size (comentario)
    // HTTP 201 Created al crear exitosamente
    // Internamente llama a propiedad-service para verificar que la propiedad existe
    @PostMapping
    public ResponseEntity<Resena> create(@Valid @RequestBody Resena resena) {
        return ResponseEntity.status(201).body(resenaService.create(resena));
    }

    // PUT /api/v1/resenas/{id} → actualiza calificación y/o comentario
    @PutMapping("/{id}")
    public ResponseEntity<Resena> update(@PathVariable Long id,
                                          @Valid @RequestBody Resena datos) {
        return ResponseEntity.ok(resenaService.update(id, datos));
    }

    // DELETE /api/v1/resenas/{id} → elimina la reseña permanentemente
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        resenaService.delete(id);
        return ResponseEntity.ok("Reseña eliminada correctamente");
    }
}