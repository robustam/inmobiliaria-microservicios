package com.inmobiliaria.resenaservice.controller;

import com.inmobiliaria.resenaservice.dto.request.ResenaRequest;
import com.inmobiliaria.resenaservice.dto.response.ResenaResponse;
import com.inmobiliaria.resenaservice.service.ResenaService;
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
@RequestMapping("/api/v1/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private static final Logger log =
            LoggerFactory.getLogger(ResenaController.class);

    private final ResenaService resenaService;

    // POST /api/v1/resenas
    // Crea una nueva resena
    // Verifica que el usuario no haya resenado antes
    @PostMapping
    public ResponseEntity<ResenaResponse> crearResena(
            @Valid @RequestBody ResenaRequest request) {

        log.info("REQUEST crear resena → propiedad: {}",
                request.getPropiedadId());

        ResenaResponse response =
                resenaService.crearResena(request);

        log.info("RESPONSE crear resena → 201 CREATED");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET /api/v1/resenas/{id}
    // Obtiene una resena por su ID
    @GetMapping("/{id}")
    public ResponseEntity<ResenaResponse> obtenerResena(
            @PathVariable Long id) {

        log.info("REQUEST obtener resena → id: {}", id);

        return ResponseEntity.ok(
                resenaService.obtenerResena(id));
    }

    // GET /api/v1/resenas/propiedad/{propiedadId}
    // Lista todas las resenas de una propiedad
    // Cualquier usuario puede ver las resenas
    @GetMapping("/propiedad/{propiedadId}")
    public ResponseEntity<List<ResenaResponse>> listarPorPropiedad(
            @PathVariable Long propiedadId) {

        log.info("REQUEST listar resenas → propiedad: {}",
                propiedadId);

        return ResponseEntity.ok(
                resenaService.listarPorPropiedad(propiedadId));
    }

    // GET /api/v1/resenas/arrendatario/{arrendatarioId}
    // Lista todas las resenas de un arrendatario
    @GetMapping("/arrendatario/{arrendatarioId}")
    public ResponseEntity<List<ResenaResponse>> listarPorArrendatario(
            @PathVariable Long arrendatarioId) {

        log.info("REQUEST listar resenas → arrendatario: {}",
                arrendatarioId);

        return ResponseEntity.ok(
                resenaService.listarPorArrendatario(
                        arrendatarioId));
    }

    // GET /api/v1/resenas/propiedad/{propiedadId}/promedio
    // Obtiene el promedio de puntuacion de una propiedad
    // Ejemplo respuesta: 4.5
    @GetMapping("/propiedad/{propiedadId}/promedio")
    public ResponseEntity<Double> obtenerPromedio(
            @PathVariable Long propiedadId) {

        log.info("REQUEST promedio → propiedad: {}",
                propiedadId);

        return ResponseEntity.ok(
                resenaService.obtenerPromedio(propiedadId));
    }

    // DELETE /api/v1/resenas/{id}
    // Elimina una resena
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarResena(
            @PathVariable Long id) {

        log.info("REQUEST eliminar resena → id: {}", id);

        resenaService.eliminarResena(id);

        return ResponseEntity.ok(
                "Resena con id " + id +
                        " eliminada correctamente");
    }
}