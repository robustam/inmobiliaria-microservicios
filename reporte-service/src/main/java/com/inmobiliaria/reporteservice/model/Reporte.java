
package com.inmobiliaria.reporteservice.model;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

// ¿Para que sirve este paquete model?
// Representa la tabla reportes en MySQL
// Un reporte es un resumen de informacion
// que genera el administrador para ver
// estadisticas del sistema
// Ejemplo: cuantas reservas hubo este mes
// Ejemplo: cuales propiedades tienen mas reservas
@Entity
@Table(name = "reportes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reporte {

    // Clave primaria con AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tipo de reporte generado
    // Usa enum para limitar los valores posibles
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoReporte tipo;

    // Fecha de inicio del periodo analizado
    // Ejemplo: 2026-01-01
    @Column(nullable = false)
    private LocalDate fechaInicio;

    // Fecha de fin del periodo analizado
    // Ejemplo: 2026-01-31
    @Column(nullable = false)
    private LocalDate fechaFin;

    // ID del usuario que genero el reporte
    // Solo ADMIN puede generar reportes
    @Column(nullable = false)
    private Long generadoPor;

    // Resultado del reporte en formato JSON
    // Guardamos el resumen como texto JSON en MySQL
    // Ejemplo: {"totalReservas": 45, "ingresos": 2250000}
    @Column(columnDefinition = "TEXT")
    private String resultado;

    // Fecha y hora en que se genero el reporte
    @Column(nullable = false)
    private LocalDateTime fechaCreacion =
            LocalDateTime.now();

    // Tipos posibles de reporte
    public enum TipoReporte {
        RESERVAS,      // estadisticas de reservas
        PROPIEDADES,   // estadisticas de propiedades
        USUARIOS,      // estadisticas de usuarios
        INGRESOS       // estadisticas de ingresos
    }
}
