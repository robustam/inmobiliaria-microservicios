package com.inmobiliaria.reservaservice.model; // Paquete del modelo de datos

// ============================================================
// ENTIDAD: RESERVA (Reserva Service)
// ============================================================
// Representa la tabla "reservas" en la base de datos reserva_db.
// Una reserva conecta un USUARIO con una PROPIEDAD para un período
// de tiempo determinado (fechaInicio → fechaFin).
//
// Es el corazón de la transacción del arriendo:
//   1. Usuario solicita reservar una propiedad
//   2. Sistema verifica que la propiedad esté DISPONIBLE
//   3. Se crea la reserva en estado PENDIENTE
//   4. La propiedad pasa a ARRENDADA
//   5. La reserva puede cambiar a CONFIRMADA, CANCELADA o COMPLETADA
//
// Tabla en MySQL: reservas
// ============================================================

import jakarta.persistence.*;            // @Entity, @Table, @Id, @Column, @PrePersist, @PreUpdate
import jakarta.validation.constraints.*; // @NotNull, @Positive
import lombok.*;                          // @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
import java.math.BigDecimal;              // Para el monto del arriendo
import java.time.LocalDate;               // Fecha sin hora (solo año-mes-día)
import java.time.LocalDateTime;           // Fecha con hora para auditoría

@Entity
@Table(name = "reservas") // nombre de la tabla en MySQL
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    // ── Enum: estados del ciclo de vida de una reserva ──────
    // Los estados siguen este flujo (no todos los cambios son válidos):
    //   PENDIENTE → CONFIRMADA → COMPLETADA
    //   PENDIENTE → CANCELADA
    //   CONFIRMADA → CANCELADA
    public enum EstadoReserva {
        PENDIENTE,   // reserva recién creada, esperando confirmación
        CONFIRMADA,  // arriendo confirmado entre ambas partes
        CANCELADA,   // reserva cancelada (libera la propiedad)
        COMPLETADA   // arriendo finalizado (período cumplido, libera la propiedad)
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incremento MySQL
    private Long id; // identificador único de la reserva

    // propiedadId: referencia a la propiedad en propiedad-service.
    // No es @ManyToOne porque cada servicio tiene su propia BD.
    @NotNull(message = "El ID de la propiedad es obligatorio")
    @Column(nullable = false)
    private Long propiedadId; // ID de la propiedad a arrendar

    // usuarioId: referencia al usuario en usuario-service.
    @NotNull(message = "El ID del usuario es obligatorio")
    @Column(nullable = false)
    private Long usuarioId; // ID del arrendatario

    // LocalDate: solo la fecha (sin hora). Ideal para períodos de arriendo.
    // Ejemplo: 2025-07-01 (1 de julio de 2025)
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaInicio; // primer día del arriendo

    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaFin; // último día del arriendo

    // @Enumerated(EnumType.STRING): guarda "PENDIENTE", "CONFIRMADA", etc. en la BD.
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoReserva estado = EstadoReserva.PENDIENTE; // toda nueva reserva inicia PENDIENTE

    // monto: precio acordado del arriendo. Puede tomarse del precio de la propiedad.
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal monto; // total a pagar por el período de arriendo

    private String comentario; // nota adicional del arrendatario (opcional)

    private LocalDateTime createdAt; // fecha de creación de la reserva
    private LocalDateTime updatedAt; // fecha de la última modificación

    // @PrePersist: se ejecuta antes del primer INSERT.
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (estado == null) estado = EstadoReserva.PENDIENTE; // garantía de estado inicial
    }

    // @PreUpdate: se ejecuta antes de cada UPDATE.
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}