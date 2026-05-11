package com.inmobiliaria.notificacionservice.model;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// ¿Para que sirve este paquete model?
// Representa la tabla notificaciones en MySQL
// Una notificacion es un aviso que se le envia
// a un usuario cuando ocurre algo importante
// Ejemplo: "Tu reserva fue aprobada"
// Ejemplo: "Tienes una nueva solicitud de reserva"
@Entity
@Table(name = "notificaciones")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notificacion {

    // Clave primaria con AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del usuario que recibe la notificacion
    // Referencia a usuario-service
    @Column(nullable = false)
    private Long usuarioId;

    // Tipo de notificacion — define el motivo del aviso
    // Usamos enum para limitar los valores posibles
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo tipo;

    // Mensaje descriptivo de la notificacion
    // Ejemplo: "Tu reserva #5 fue aprobada por el arrendador"
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    // false = notificacion nueva sin leer
    // true  = usuario ya la vio
    // Por defecto nace sin leer
    @Column(nullable = false)
    private Boolean leido = false;

    // Fecha y hora en que se creo la notificacion
    @Column(nullable = false)
    private LocalDateTime fechaCreacion =
            LocalDateTime.now();

    // Tipos posibles de notificacion
    // Define el motivo por el cual se genero el aviso
    public enum Tipo {
        RESERVA_APROBADA,   // arrendador aprobo la reserva
        RESERVA_RECHAZADA,  // arrendador rechazo la reserva
        RESERVA_CANCELADA,  // arrendatario cancelo la reserva
        NUEVA_RESERVA,      // nueva solicitud de reserva
        NUEVA_RESENA        // nuevo comentario en propiedad
    }
}