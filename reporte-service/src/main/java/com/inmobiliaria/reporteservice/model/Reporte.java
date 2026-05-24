package com.inmobiliaria.reporteservice.model; // Paquete del modelo de datos

// ============================================================
// ENTIDAD: REPORTE (Reporte Service)
// ============================================================
// Representa la tabla "reportes" en la base de datos reporte_db.
// Un reporte es un resumen estadístico generado por el sistema
// sobre propiedades, reservas o ingresos.
//
// El sistema genera reportes consultando otros microservicios
// via Feign (propiedad-service y reserva-service) y guarda
// un historial de cada reporte generado.
//
// Tabla en MySQL: reportes
// ============================================================

import jakarta.persistence.*;            // Anotaciones JPA
import jakarta.validation.constraints.*; // @NotBlank, @NotNull, @Size
import lombok.*;                          // @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
import java.time.LocalDateTime;           // Fecha y hora

@Entity
@Table(name = "reportes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reporte {

    // ── Enum: tipos de reportes disponibles ─────────────────
    public enum TipoReporte {
        PROPIEDADES, // estadísticas de propiedades (por estado, tipo, ciudad)
        RESERVAS,    // estadísticas de reservas (por estado, ingresos totales)
        INGRESOS,    // reporte financiero de arriendos completados
        GENERAL      // combinación de todos los reportes anteriores
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incremento
    private Long id;

    // titulo: nombre descriptivo del reporte.
    @NotBlank(message = "El título del reporte es obligatorio")
    @Column(nullable = false)
    private String titulo; // ej: "Resumen de Propiedades - Mayo 2025"

    // tipo: categoría del reporte (PROPIEDADES, RESERVAS, etc.)
    @NotNull(message = "El tipo de reporte es obligatorio")
    @Enumerated(EnumType.STRING) // guarda "PROPIEDADES", "RESERVAS", etc. en BD
    @Column(nullable = false)
    private TipoReporte tipo;

    // descripcion: resumen de qué contiene el reporte.
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion; // ej: "Estadísticas generales de propiedades"

    // datos: los datos del reporte en formato String (JSON o texto).
    // columnDefinition = "TEXT": permite guardar grandes cantidades de datos.
    @Column(columnDefinition = "TEXT")
    private String datos; // los resultados del reporte serializados como texto

    private Long generadoPor; // ID del administrador que solicitó el reporte

    private LocalDateTime generadoEn; // fecha y hora en que se generó el reporte

    // @PrePersist: asigna la fecha automáticamente antes del INSERT.
    @PrePersist
    protected void onCreate() {
        generadoEn = LocalDateTime.now();
    }
}