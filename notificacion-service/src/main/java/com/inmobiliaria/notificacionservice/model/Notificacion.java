package com.inmobiliaria.notificacionservice.model; // Paquete del modelo de datos

// ============================================================
// ENTIDAD: NOTIFICACIÓN (Notificacion Service)
// ============================================================
// Representa la tabla "notificaciones" en la base de datos notificacion_db.
// Una notificación es un mensaje del sistema dirigido a un usuario específico.
//
// Ejemplos de notificaciones:
//   BIENVENIDA: "¡Bienvenido al sistema, Juan!"
//   RESERVA:    "Tu reserva #5 ha sido confirmada"
//   RESENA:     "Tu propiedad recibió una reseña"
//   PAGO:       "Recordatorio de pago de arriendo"
//   SISTEMA:    "Mantenimiento programado el 15/06"
//
// Tabla en MySQL: notificaciones
// ============================================================

import jakarta.persistence.*;            // Anotaciones JPA
import jakarta.validation.constraints.*; // @NotNull, @NotBlank
import lombok.*;                          // @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
import java.time.LocalDateTime;           // Fecha con hora

@Entity
@Table(name = "notificaciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    // ── Enum: tipos de notificación del sistema ──────────────
    // Permite categorizar y filtrar notificaciones por tipo.
    public enum TipoNotificacion {
        RESERVA,     // relacionada con una reserva de arriendo
        RESENA,      // relacionada con una reseña recibida
        SISTEMA,     // mensaje administrativo o del sistema
        PAGO,        // relacionada con pagos de arriendo
        BIENVENIDA   // mensaje de bienvenida al registrarse
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incremento
    private Long id;

    // usuarioId: destinatario de la notificación.
    // Referencia a usuario-service (sin @ManyToOne por arquitectura de microservicios).
    @NotNull(message = "El ID del usuario es obligatorio")
    @Column(nullable = false)
    private Long usuarioId;

    // titulo: asunto corto de la notificación (como el subject de un email).
    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false)
    private String titulo;

    // mensaje: contenido completo de la notificación.
    // columnDefinition = "TEXT": permite mensajes largos.
    @NotBlank(message = "El mensaje es obligatorio")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String mensaje;

    // tipo: categoría de la notificación.
    @Enumerated(EnumType.STRING) // guarda "RESERVA", "SISTEMA", etc. en BD (no número)
    @Builder.Default
    private TipoNotificacion tipo = TipoNotificacion.SISTEMA; // por defecto SISTEMA

    // leida: indica si el usuario ya vio la notificación.
    // false = no leída (aparece como nueva/pendiente).
    // true  = leída (ya fue vista por el usuario).
    @Builder.Default
    private boolean leida = false; // todas las notificaciones nuevas están sin leer

    private LocalDateTime createdAt; // cuándo se creó la notificación
    private LocalDateTime leidaAt;  // cuándo el usuario la marcó como leída (null si no leída)

    // @PrePersist: asigna valores automáticos antes del primer INSERT.
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (tipo == null) tipo = TipoNotificacion.SISTEMA; // garantía de tipo por defecto
    }
}