
package com.inmobiliaria.reporteservice.dto.request;

import com.inmobiliaria.reporteservice.model.Reporte;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

// ¿Para que sirve este DTO?
// Define exactamente que datos debe enviar el cliente
// para generar un reporte
// Solo el ADMIN puede generar reportes
@Data
public class ReporteRequest {

    // Tipo de reporte que se quiere generar
    // Debe ser uno de los valores del enum TipoReporte
    @NotNull(message = "El tipo de reporte es obligatorio")
    private Reporte.TipoReporte tipo;

    // Fecha de inicio del periodo a analizar
    // Ejemplo: 2026-01-01
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    // Fecha de fin del periodo a analizar
    // Ejemplo: 2026-01-31
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    // ID del administrador que genera el reporte
    @NotNull(message = "El id del generador es obligatorio")
    private Long generadoPor;
}