package com.inmobiliaria.reporteservice.controller; // Paquete del controlador

// ============================================================
// CONTROLADOR HTTP: REPORTE SERVICE
// ============================================================
// Endpoints para generar y consultar reportes del sistema.
// Solo disponible para administradores.
//
// Endpoints (todos bajo /api/v1/reportes):
//   GET /health       → estado del servicio
//   GET /             → historial de todos los reportes generados
//   GET /{id}         → obtener reporte específico por ID
//   GET /propiedades  → generar reporte de propiedades (consulta propiedad-service)
//   GET /reservas     → generar reporte de reservas (consulta reserva-service)
//   GET /general      → generar reporte combinado (propiedades + reservas)
//   DELETE /{id}      → eliminar un reporte del historial
// ============================================================

import com.inmobiliaria.reporteservice.model.Reporte;           // Entidad
import com.inmobiliaria.reporteservice.service.ReporteService;  // Lógica de negocio
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/health")
    public String health() {
        return "Reporte Service is UP! ✅";
    }

    // GET /api/v1/reportes → historial de reportes (más recientes primero)
    @GetMapping
    public ResponseEntity<List<Reporte>> getReportes() {
        return ResponseEntity.ok(reporteService.findAll());
    }

    // GET /api/v1/reportes/{id} → reporte específico por ID
    @GetMapping("/{id}")
    public ResponseEntity<Reporte> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.findById(id));
    }

    // GET /api/v1/reportes/propiedades
    // Genera y retorna estadísticas de propiedades.
    // Al mismo tiempo guarda el reporte en el historial.
    // Respuesta: { "totalPropiedades": 9, "porEstado": {...}, "porTipo": {...}, "porCiudad": {...} }
    @GetMapping("/propiedades")
    public ResponseEntity<Map<String, Object>> resumenPropiedades() {
        return ResponseEntity.ok(reporteService.generarResumenPropiedades());
    }

    // GET /api/v1/reportes/reservas
    // Genera y retorna estadísticas de reservas e ingresos.
    // Respuesta: { "totalReservas": 15, "porEstado": {...}, "ingresoTotal": 1500000 }
    @GetMapping("/reservas")
    public ResponseEntity<Map<String, Object>> resumenReservas() {
        return ResponseEntity.ok(reporteService.generarResumenReservas());
    }

    // GET /api/v1/reportes/general
    // Genera y retorna el reporte completo del sistema.
    // Respuesta: { "propiedades": {...}, "reservas": {...} }
    @GetMapping("/general")
    public ResponseEntity<Map<String, Object>> resumenGeneral() {
        return ResponseEntity.ok(reporteService.generarResumenGeneral());
    }

    // DELETE /api/v1/reportes/{id} → elimina un reporte del historial
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        reporteService.delete(id);
        return ResponseEntity.ok("Reporte eliminado correctamente");
    }
}