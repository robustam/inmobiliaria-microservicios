package com.inmobiliaria.reporteservice.client; // Paquete de clientes Feign

// ============================================================
// FEIGN CLIENT: RESERVA (desde Reporte Service)
// ============================================================
// Permite que reporte-service obtenga todas las reservas para
// calcular estadísticas de arriendos e ingresos.
//
// Feign: GET http://reserva-service/api/v1/reservas
// ============================================================

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.math.BigDecimal;
import java.util.List;

// @FeignClient(name = "reserva-service"): usa Eureka para encontrar reserva-service.
@FeignClient(name = "reserva-service")
public interface ReservaClient {

    // Obtiene TODAS las reservas del sistema.
    // Mapea a: GET /api/v1/reservas en reserva-service.
    // Se usa para calcular: total, por estado, ingresos de COMPLETADAS.
    @GetMapping("/api/v1/reservas")
    List<ReservaDTO> findAll();

    // ── DTO: campos necesarios para las estadísticas de reservas ──
    @Data
    class ReservaDTO {
        private Long id;              // ID de la reserva
        private Long propiedadId;     // propiedad reservada
        private Long usuarioId;       // arrendatario
        private String estado;        // "PENDIENTE", "CONFIRMADA", "CANCELADA", "COMPLETADA"
        private BigDecimal monto;     // precio del arriendo (para calcular ingresos totales)
        private String fechaInicio;   // inicio del período de arriendo
        private String fechaFin;      // fin del período de arriendo
    }
}