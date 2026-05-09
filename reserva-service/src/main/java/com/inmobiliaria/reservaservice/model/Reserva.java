package com.inmobiliaria.reservaservice.model;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

// ¿Para que sirve este paquete model?
// Representa la tabla reservas en MySQL
// Cada atributo = una columna en la tabla
// Spring crea la tabla automaticamente al arrancar
@Entity
@Table(name = "reservas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reserva {

    // Clave primaria con AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID de la propiedad que se quiere reservar
    // No es relacion JPA porque propiedad vive
    // en otro microservicio (propiedad-service)
    @Column(nullable = false)
    private Long propiedadId;

    // ID del arrendatario que hace la reserva
    // Referencia al usuario en usuario-service
    @Column(nullable = false)
    private Long arrendatarioId;

    // Fecha de inicio del arriendo
    // LocalDate guarda solo fecha: 2026-01-15
    @Column(nullable = false)
    private LocalDate fechaInicio;

    // Fecha de fin del arriendo
    @Column(nullable = false)
    private LocalDate fechaFin;

    // Estado actual de la reserva
    // Usa enum para limitar los valores posibles
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.PENDIENTE;

    // Mensaje opcional del arrendatario
    // Ejemplo: "Somos familia de 3 personas"
    @Column(columnDefinition = "TEXT")
    private String mensajeSolicitud;

    // Fecha y hora en que se creo la reserva
    // LocalDateTime guarda fecha y hora: 2026-01-15T10:30:00
    @Column(nullable = false)
    private LocalDateTime fechaCreacion =
            LocalDateTime.now();

    // Estados posibles de una reserva
    // Una reserva siempre empieza en PENDIENTE
    public enum Estado {
        PENDIENTE,   // esperando respuesta del arrendador
        APROBADA,    // arrendador acepto la reserva
        RECHAZADA,   // arrendador rechazo la reserva
        CANCELADA    // arrendatario cancelo la reserva
    }
}