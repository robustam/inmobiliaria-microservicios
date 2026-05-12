
package com.inmobiliaria.reporteservice.dto.response;

import com.inmobiliaria.reporteservice.model.Reporte;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

// ¿Para que sirve este DTO?
// Define exactamente que datos devuelve el servidor
// cuando el cliente consulta un reporte
// Nunca devolvemos la entidad directamente
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteResponse {

    // ID unico del reporte generado por MySQL
    private Long id;

    // Tipo de reporte generado
    // RESERVAS, PROPIEDADES, USUARIOS, INGRESOS
    private Reporte.TipoReporte tipo;

    // Periodo analizado en el reporte
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    // ID del administrador que genero el reporte
    private Long generadoPor;

    // Resultado del reporte en formato texto JSON
    // Contiene las estadisticas calculadas
    // Ejemplo: {"totalReservas": 45, "aprobadas": 30}
    private String resultado;

    // Fecha y hora en que se genero el reporte
    private LocalDateTime fechaCreacion;
}
