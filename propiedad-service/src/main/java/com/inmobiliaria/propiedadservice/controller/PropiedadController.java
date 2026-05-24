package com.inmobiliaria.propiedadservice.controller; // Paquete del controlador

// ============================================================
// CONTROLADOR HTTP: PROPIEDAD SERVICE
// ============================================================
// Expone los endpoints REST para gestionar propiedades.
// Recibe peticiones HTTP del API Gateway y las delega al Service.
//
// Endpoints (todos bajo /api/v1/propiedades):
//   GET    /health                 → verificar que el servicio está vivo
//   GET    /                       → listar propiedades DISPONIBLES
//   GET    /todas                  → listar TODAS las propiedades
//   GET    /{id}                   → obtener una propiedad por ID
//   GET    /propietario/{id}       → propiedades de un propietario
//   GET    /region/{region}        → propiedades en una región
//   GET    /comuna/{comuna}        → propiedades en una comuna
//   GET    /buscar?region=...      → búsqueda avanzada con filtros
//   POST   /                       → crear nueva propiedad
//   PUT    /{id}                   → actualizar propiedad
//   PATCH  /{id}/estado?estado=... → cambiar estado de propiedad
//   DELETE /{id}                   → desactivar propiedad
// ============================================================

import com.inmobiliaria.propiedadservice.model.Propiedad;        // Entidad
import com.inmobiliaria.propiedadservice.service.PropiedadService; // Lógica de negocio
import jakarta.validation.Valid;          // Activa validaciones de la entidad (@NotBlank, etc.)
import lombok.RequiredArgsConstructor;    // Genera constructor para inyección
import org.springframework.http.ResponseEntity; // Respuesta HTTP con código de estado
import org.springframework.web.bind.annotation.*; // @RestController, @GetMapping, etc.
import java.math.BigDecimal; // Para el parámetro precioMin/precioMax en búsqueda
import java.util.List;       // Lista de propiedades

@RestController
@RequestMapping("/api/v1/propiedades") // prefijo de URL para todos los endpoints
@RequiredArgsConstructor
public class PropiedadController {

    private final PropiedadService propiedadService; // servicio con la lógica de negocio

    // GET /api/v1/propiedades/health
    // Verifica que el microservicio está corriendo.
    @GetMapping("/health")
    public String health() {
        return "Propiedad Service is UP! ✅";
    }

    // GET /api/v1/propiedades
    // Retorna lista de propiedades DISPONIBLES (lo que ven los arrendatarios).
    // HTTP 200 OK + array JSON de propiedades.
    @GetMapping
    public ResponseEntity<List<Propiedad>> getPropiedades() {
        return ResponseEntity.ok(propiedadService.findDisponibles());
    }

    // GET /api/v1/propiedades/todas
    // Retorna TODAS las propiedades (admin: incluye ARRENDADAS e INACTIVAS).
    @GetMapping("/todas")
    public ResponseEntity<List<Propiedad>> getAll() {
        return ResponseEntity.ok(propiedadService.findAll());
    }

    // GET /api/v1/propiedades/{id}
    // Retorna una propiedad específica por su ID.
    // Si no existe → GlobalExceptionHandler → HTTP 404.
    // @PathVariable Long id: extrae el {id} de la URL y lo convierte a Long.
    @GetMapping("/{id}")
    public ResponseEntity<Propiedad> getById(@PathVariable Long id) {
        return ResponseEntity.ok(propiedadService.findById(id));
    }

    // GET /api/v1/propiedades/propietario/{propietarioId}
    // Retorna todas las propiedades publicadas por un propietario.
    // Usado por el panel de administración del propietario.
    @GetMapping("/propietario/{propietarioId}")
    public ResponseEntity<List<Propiedad>> getByPropietario(@PathVariable Long propietarioId) {
        return ResponseEntity.ok(propiedadService.findByPropietario(propietarioId));
    }

    // GET /api/v1/propiedades/region/{region}
    // Retorna propiedades DISPONIBLES en la región indicada.
    // Ejemplo: /api/v1/propiedades/region/Metropolitana
    @GetMapping("/region/{region}")
    public ResponseEntity<List<Propiedad>> getByRegion(@PathVariable String region) {
        return ResponseEntity.ok(propiedadService.findByRegion(region));
    }

    // GET /api/v1/propiedades/comuna/{comuna}
    // Retorna propiedades DISPONIBLES en la comuna indicada.
    // Ejemplo: /api/v1/propiedades/comuna/Ñuñoa
    @GetMapping("/comuna/{comuna}")
    public ResponseEntity<List<Propiedad>> getByComuna(@PathVariable String comuna) {
        return ResponseEntity.ok(propiedadService.findByComuna(comuna));
    }

    // GET /api/v1/propiedades/buscar?region=...&comuna=...&tipo=...&precioMin=...&precioMax=...
    // Búsqueda avanzada con múltiples filtros, todos opcionales.
    // Ejemplo: /buscar?region=Valparaíso&tipo=DEPARTAMENTO&precioMax=300000
    @GetMapping("/buscar")
    public ResponseEntity<List<Propiedad>> buscar(
            // @RequestParam(required = false): el parámetro es opcional en la URL.
            // Si no viene en la URL, el valor es null.
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String comuna,
            @RequestParam(required = false) String tipo,        // "CASA" o "DEPARTAMENTO"
            @RequestParam(required = false) BigDecimal precioMin, // precio mínimo de arriendo
            @RequestParam(required = false) BigDecimal precioMax) { // precio máximo de arriendo
        return ResponseEntity.ok(propiedadService.buscar(region, ciudad, comuna, tipo, precioMin, precioMax));
    }

    // POST /api/v1/propiedades
    // Crea una nueva propiedad en el sistema.
    // @Valid: valida la entidad (lanza HTTP 400 si algún campo falla @NotBlank, etc.)
    // @RequestBody: deserializa el JSON del body al objeto Propiedad.
    // HTTP 201 Created (estándar para recursos creados exitosamente).
    @PostMapping
    public ResponseEntity<Propiedad> create(@Valid @RequestBody Propiedad propiedad) {
        return ResponseEntity.status(201).body(propiedadService.create(propiedad));
    }

    // PUT /api/v1/propiedades/{id}
    // Actualiza todos los datos de una propiedad existente.
    // PUT = reemplaza el recurso completo (todos los campos deben venir en el body).
    @PutMapping("/{id}")
    public ResponseEntity<Propiedad> update(@PathVariable Long id,
                                             @Valid @RequestBody Propiedad datos) {
        return ResponseEntity.ok(propiedadService.update(id, datos));
    }

    // PATCH /api/v1/propiedades/{id}/estado?estado=ARRENDADA
    // Cambia SOLO el estado de una propiedad (actualización parcial).
    // PATCH = actualización parcial (diferente a PUT que reemplaza todo).
    // Usado por reserva-service para marcar propiedades como ARRENDADA/DISPONIBLE.
    // @RequestParam String estado: recibe el estado como parámetro de URL.
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Propiedad> cambiarEstado(@PathVariable Long id,
                                                    @RequestParam String estado) {
        return ResponseEntity.ok(propiedadService.cambiarEstado(id, estado));
    }

    // DELETE /api/v1/propiedades/{id}
    // "Elimina" (desactiva) una propiedad (borrado lógico: cambia estado a INACTIVA).
    // No borra el registro de la BD para conservar historial.
    // HTTP 200 + mensaje de confirmación.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        propiedadService.delete(id);
        return ResponseEntity.ok("Propiedad desactivada correctamente");
    }
}