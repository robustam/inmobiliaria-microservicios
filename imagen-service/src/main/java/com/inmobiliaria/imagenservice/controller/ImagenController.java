package com.inmobiliaria.imagenservice.controller; // Paquete del controlador

// ============================================================
// CONTROLADOR HTTP: IMAGEN SERVICE
// ============================================================
// Endpoints para gestionar las imágenes de propiedades.
//
// Endpoints (todos bajo /api/v1/imagenes):
//   GET    /health                         → estado del servicio
//   GET    /                               → listar todas las imágenes
//   GET    /{id}                           → obtener imagen por ID
//   GET    /propiedad/{propiedadId}        → imágenes de una propiedad
//   GET    /propiedad/{propiedadId}/principal → imagen principal de una propiedad
//   POST   /                               → registrar nueva imagen
//   PATCH  /{id}/principal                 → establecer como imagen principal
//   DELETE /{id}                           → eliminar una imagen
//   DELETE /propiedad/{propiedadId}        → eliminar todas las imágenes de una propiedad
// ============================================================

import com.inmobiliaria.imagenservice.model.Imagen;          // Entidad
import com.inmobiliaria.imagenservice.service.ImagenService; // Lógica de negocio
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/imagenes")
@RequiredArgsConstructor
public class ImagenController {

    private final ImagenService imagenService;

    @GetMapping("/health")
    public String health() {
        return "Imagen Service is UP! ✅";
    }

    // GET /api/v1/imagenes → lista todas las imágenes del sistema
    @GetMapping
    public ResponseEntity<List<Imagen>> getImagenes() {
        return ResponseEntity.ok(imagenService.findAll());
    }

    // GET /api/v1/imagenes/{id} → imagen específica por ID
    @GetMapping("/{id}")
    public ResponseEntity<Imagen> getById(@PathVariable Long id) {
        return ResponseEntity.ok(imagenService.findById(id));
    }

    // GET /api/v1/imagenes/propiedad/{propiedadId}
    // Imágenes de una propiedad: la principal aparece primero, luego el resto.
    @GetMapping("/propiedad/{propiedadId}")
    public ResponseEntity<List<Imagen>> getByPropiedad(@PathVariable Long propiedadId) {
        return ResponseEntity.ok(imagenService.findByPropiedad(propiedadId));
    }

    // GET /api/v1/imagenes/propiedad/{propiedadId}/principal
    // Retorna solo la foto de portada de la propiedad.
    // HTTP 404 si no hay imagen principal configurada.
    @GetMapping("/propiedad/{propiedadId}/principal")
    public ResponseEntity<Imagen> getPrincipal(@PathVariable Long propiedadId) {
        return ResponseEntity.ok(imagenService.findPrincipalByPropiedad(propiedadId));
    }

    // POST /api/v1/imagenes → registra una nueva imagen
    // @Valid: valida @NotNull(propiedadId) y @NotBlank(url)
    // HTTP 201 Created al crear exitosamente
    @PostMapping
    public ResponseEntity<Imagen> create(@Valid @RequestBody Imagen imagen) {
        return ResponseEntity.status(201).body(imagenService.create(imagen));
    }

    // PATCH /api/v1/imagenes/{id}/principal
    // Marca esta imagen como la foto de portada de su propiedad.
    // Desactiva automáticamente la imagen principal anterior.
    @PatchMapping("/{id}/principal")
    public ResponseEntity<Imagen> setPrincipal(@PathVariable Long id) {
        return ResponseEntity.ok(imagenService.setPrincipal(id));
    }

    // DELETE /api/v1/imagenes/{id} → elimina una imagen específica
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        imagenService.delete(id);
        return ResponseEntity.ok("Imagen eliminada correctamente");
    }

    // DELETE /api/v1/imagenes/propiedad/{propiedadId}
    // Elimina TODAS las imágenes de una propiedad.
    // Usado cuando se desactiva o elimina una propiedad.
    @DeleteMapping("/propiedad/{propiedadId}")
    public ResponseEntity<String> deleteByPropiedad(@PathVariable Long propiedadId) {
        imagenService.deleteByPropiedad(propiedadId);
        return ResponseEntity.ok("Imágenes de la propiedad eliminadas");
    }
}