
package com.inmobiliaria.reporteservice.controller;

import com.inmobiliaria.reporteservice.dto.request.ReporteRequest;
import com.inmobiliaria.reporteservice.dto.response.ReporteResponse;
import com.inmobiliaria.reporteservice.model.Reporte;
import com.inmobiliaria.reporteservice.service.ReporteService;
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
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private static final Logger log =
            LoggerFactory.getLogger(ReporteController.class);

    private final ReporteService reporteService;

    // POST /api/v1/reportes/generar
    // Genera un nuevo reporte del sistema
    // Solo el ADMIN puede generar reportes
    @PostMapping("/generar")
    public ResponseEntity<ReporteResponse> generarReporte(
            @Valid @RequestBody ReporteRequest request) {

        log.info("REQUEST generar reporte → tipo: {}",
                request.getTipo());

        ReporteResponse response =
                reporteService.generarReporte(request);

        log.info("RESPONSE generar reporte → 201 CREATED");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET /api/v1/reportes/{id}
    // Obtiene un reporte por su ID
    @GetMapping("/{id}")
    public ResponseEntity<ReporteResponse> obtenerReporte(
            @PathVariable Long id) {

        log.info("REQUEST obtener reporte → id: {}", id);

        return ResponseEntity.ok(
                reporteService.obtenerReporte(id));
    }

    // GET /api/v1/reportes
    // Lista todos los reportes del sistema
    @GetMapping
    public ResponseEntity<List<ReporteResponse>> listarReportes() {

        log.info("REQUEST listar todos los reportes");

        return ResponseEntity.ok(
                reporteService.listarReportes());
    }

    // GET /api/v1/reportes/tipo/{tipo}
    // Lista reportes por tipo especifico
    // Ejemplo: /reportes/tipo/RESERVAS
    // Ejemplo: /reportes/tipo/PROPIEDADES
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ReporteResponse>> listarPorTipo(
            // @PathVariable extrae el tipo de la URL
            // Spring convierte automaticamente el texto
            // al enum TipoReporte
            @PathVariable Reporte.TipoReporte tipo) {

        log.info("REQUEST listar reportes → tipo: {}", tipo);

        return ResponseEntity.ok(
                reporteService.listarPorTipo(tipo));
    }
}
