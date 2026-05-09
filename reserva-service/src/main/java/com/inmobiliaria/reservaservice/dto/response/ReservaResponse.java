
package com.inmobiliaria.reservaservice.dto.response;

import com.inmobiliaria.reservaservice.model.Reserva;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

// ¿Para que sirve este DTO?
// Define exactamente que datos devuelve el servidor
// cuando el cliente consulta una reserva
// Nunca devolvemos la entidad Reserva directamente
// Con este DTO controlamos que informacion exponemos
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaResponse {

    // ID unico de la reserva generado por MySQL
    private Long id;

    // ID de la propiedad reservada
    private Long propiedadId;

    // ID del arrendatario que hizo la reserva
    private Long arrendatarioId;

    // Fechas del periodo de arriendo
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    // Estado actual de la reserva
    // Puede ser: PENDIENTE, APROBADA, RECHAZADA, CANCELADA
    // Usamos el enum del modelo para consistencia
    private Reserva.Estado estado;

    // Mensaje del arrendatario — puede ser null
    private String mensajeSolicitud;

    // Fecha y hora exacta en que se creo la reserva
    private LocalDateTime fechaCreacion;
}
