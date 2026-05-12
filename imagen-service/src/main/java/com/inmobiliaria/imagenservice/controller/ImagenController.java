
package com.inmobiliaria.imagenservice.controller;

import com.inmobiliaria.imagenservice.dto.request.ImagenRequest;
import com.inmobiliaria.imagenservice.dto.response.ImagenResponse;
import com.inmobiliaria.imagenservice.model.Imagen;
import com.inmobiliaria.imagenservice.service.ImagenService;
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
@RequestMapping("/api/v1/imagenes")
@RequiredArgsConstructor
public class ImagenController {

    private static final Logger log =
            LoggerFactory.getLogger(ImagenController.class);

    private final ImagenService imagenService;

    // POST /api/v1/imagenes
    // Registra una nueva imagen en el sistema
    // El cliente envia la URL de la imagen ya subida
    @PostMapping
    public ResponseEntity<ImagenResponse> guardarImagen(
            @Valid @RequestBody ImagenRequest request) {

        log.info("REQUEST guardar imagen → entidad: {}",
                request.getEntidadId());

        ImagenResponse response =
                imagenService.guardarImagen(request);

        log.info("RESPONSE guardar imagen → 201 CREATED");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET /api/v1/imagenes/{id}
    // Obtiene una imagen por su ID
    @GetMapping("/{id}")
    public ResponseEntity<ImagenResponse> obtenerImagen(
            @PathVariable Long id) {

        log.info("REQUEST obtener imagen → id: {}", id);

        return ResponseEntity.ok(
                imagenService.obtenerImagen(id));
    }

    // GET /api/v1/imagenes/entidad/{entidadId}?tipo=PROPIEDAD
    // Lista todas las imagenes de una entidad
    // El tipo se envia como parametro en la URL
    // Ejemplo: /imagenes/entidad/5?tipo=PROPIEDAD
    @GetMapping("/entidad/{entidadId}")
    public ResponseEntity<List<ImagenResponse>> listarPorEntidad(
            // @PathVariable extrae el id de la URL
            @PathVariable Long entidadId,
            // @RequestParam lee el parametro ?tipo=PROPIEDAD
            @RequestParam Imagen.TipoEntidad tipo) {

        log.info("REQUEST listar imagenes → entidad: {} tipo: {}",
                entidadId, tipo);

        return ResponseEntity.ok(
                imagenService.listarPorEntidad(
                        entidadId, tipo));
    }

    // DELETE /api/v1/imagenes/{id}
    // Elimina una imagen del sistema
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarImagen(
            @PathVariable Long id) {

        log.info("REQUEST eliminar imagen → id: {}", id);

        imagenService.eliminarImagen(id);

        return ResponseEntity.ok(
                "Imagen con id " + id +
                        " eliminada correctamente");
    }
}