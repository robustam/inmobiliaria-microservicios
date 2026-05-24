package com.inmobiliaria.resenaservice.model; // Paquete del modelo de datos

// ============================================================
// ENTIDAD: RESEÑA (Resena Service)
// ============================================================
// Representa la tabla "resenas" en la base de datos resena_db.
// Una reseña es la evaluación que hace un ARRENDATARIO sobre
// una PROPIEDAD que arrendó, con una calificación del 1 al 5.
//
// Permite a futuros arrendatarios tomar decisiones informadas
// basándose en experiencias reales de otros usuarios.
//
// Tabla en MySQL: resenas
// ============================================================

import jakarta.persistence.*;            // @Entity, @Table, @Id, @Column, @PrePersist
import jakarta.validation.constraints.*; // @NotNull, @Min, @Max, @Size
import lombok.*;                          // @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
import java.time.LocalDateTime;           // Fecha con hora para auditoría

@Entity
@Table(name = "resenas")   // nombre de la tabla en MySQL
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incremento
    private Long id; // identificador único de la reseña

    // propiedadId: propiedad evaluada en esta reseña.
    // Referencia a propiedad-service (no hay @ManyToOne entre microservicios).
    @NotNull(message = "El ID de la propiedad es obligatorio")
    @Column(nullable = false)
    private Long propiedadId;

    // usuarioId: arrendatario que escribe la reseña.
    // Referencia a usuario-service.
    @NotNull(message = "El ID del usuario es obligatorio")
    @Column(nullable = false)
    private Long usuarioId;

    private String nombreUsuario; // nombre del usuario (opcional, para mostrar en pantalla)

    // calificación del 1 al 5 estrellas.
    // @Min(1): mínimo 1 estrella.
    // @Max(5): máximo 5 estrellas.
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    @Column(nullable = false)
    private Integer calificacion; // 1=Muy malo, 2=Malo, 3=Regular, 4=Bueno, 5=Excelente

    // @Size(max = 1000): el comentario no puede superar 1000 caracteres.
    // columnDefinition = "TEXT": tipo TEXT en MySQL para textos largos.
    @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String comentario; // opinión escrita del arrendatario (opcional)

    private LocalDateTime createdAt; // fecha en que se escribió la reseña

    // @PrePersist: asigna la fecha automáticamente antes del INSERT.
    // La reseña no tiene updatedAt porque no debería modificarse después de publicada.
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}