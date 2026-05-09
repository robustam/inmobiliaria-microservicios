package com.inmobiliaria.propiedadservice.controller;

import com.inmobiliaria.propiedadservice.dto.request.PropiedadRequest;
import com.inmobiliaria.propiedadservice.dto.response.PropiedadResponse;
import com.inmobiliaria.propiedadservice.service.PropiedadService;
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
// Solo recibe, delega y retorna respuesta
@RestController
@RequestMapping("/api/v1/propiedades")
@RequiredArgsConstructor
public class PropiedadController {

    private static final Logger log =
            LoggerFactory.getLogger(PropiedadController.class);

    private final PropiedadService propiedadService;

    // POST /api/v1/propiedades
    // Crea una nueva propiedad
    @PostMapping
    public ResponseEntity<PropiedadResponse> crearPropiedad(
            @Valid @RequestBody PropiedadRequest request) {

        log.info("REQUEST crear propiedad → arrendador: {}",
                request.getArrendadorId());

        PropiedadResponse response =
                propiedadService.crearPropiedad(request);

        log.info("RESPONSE crear propiedad → 201 CREATED");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET /api/v1/propiedades/{id}
    // Obtiene una propiedad por su ID
    @GetMapping("/{id}")
    public ResponseEntity<PropiedadResponse> obtenerPropiedad(
            @PathVariable Long id) {

        log.info("REQUEST obtener propiedad → id: {}", id);

        return ResponseEntity.ok(
                propiedadService.obtenerPropiedad(id));
    }

    // GET /api/v1/propiedades
    // Lista todas las propiedades
    @GetMapping
    public ResponseEntity<List<PropiedadResponse>> listarPropiedades() {

        log.info("REQUEST listar todas las propiedades");

        return ResponseEntity.ok(
                propiedadService.listarPropiedades());
    }

    // GET /api/v1/propiedades/disponibles
    // Lista solo propiedades disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<PropiedadResponse>> listarDisponibles() {

        log.info("REQUEST listar propiedades disponibles");

        return ResponseEntity.ok(
                propiedadService.listarDisponibles());
    }

    // GET /api/v1/propiedades/arrendador/{arrendadorId}
    // Lista propiedades de un arrendador especifico
    @GetMapping("/arrendador/{arrendadorId}")
    public ResponseEntity<List<PropiedadResponse>> listarPorArrendador(
            @PathVariable Long arrendadorId) {

        log.info("REQUEST listar propiedades → arrendador: {}",
                arrendadorId);

        return ResponseEntity.ok(
                propiedadService.listarPorArrendador(arrendadorId));
    }

    // PUT /api/v1/propiedades/{id}
    // Actualiza una propiedad existente
    @PutMapping("/{id}")
    public ResponseEntity<PropiedadResponse> actualizarPropiedad(
            @PathVariable Long id,
            @Valid @RequestBody PropiedadRequest request) {

        log.info("REQUEST actualizar propiedad → id: {}", id);

        return ResponseEntity.ok(
                propiedadService.actualizarPropiedad(id, request));
    }

    // PATCH /api/v1/propiedades/{id}/disponibilidad
    // Cambia solo la disponibilidad de una propiedad
    // PATCH se usa para actualizar un solo campo
    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<PropiedadResponse> cambiarDisponibilidad(
            @PathVariable Long id,
            // @RequestParam lee el parametro de la URL
            // Ejemplo: /propiedades/1/disponibilidad?valor=false
            @RequestParam Boolean valor) {

        log.info("REQUEST cambiar disponibilidad → id: {} valor: {}",
                id, valor);

        return ResponseEntity.ok(
                propiedadService.cambiarDisponibilidad(id, valor));
    }

    // DELETE /api/v1/propiedades/{id}
    // Elimina una propiedad
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPropiedad(
            @PathVariable Long id) {

        log.info("REQUEST eliminar propiedad → id: {}", id);

        propiedadService.eliminarPropiedad(id);

        return ResponseEntity.ok(
                "Propiedad con id " + id +
                        " eliminada correctamente");
    }
}