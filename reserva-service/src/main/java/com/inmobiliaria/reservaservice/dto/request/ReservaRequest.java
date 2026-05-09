
package com.inmobiliaria.reservaservice.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

// ¿Para que sirve este DTO?
// Define exactamente que datos debe enviar el cliente
// para crear una reserva
// Tiene validaciones para proteger la base de datos
@Data
public class ReservaRequest {

    // ID de la propiedad que se quiere reservar
    // @NotNull porque es un numero no un texto
    @NotNull(message = "El id de la propiedad es obligatorio")
    private Long propiedadId;

    // ID del arrendatario que hace la reserva
    @NotNull(message = "El id del arrendatario es obligatorio")
    private Long arrendatarioId;

    // Fecha de inicio del arriendo
    // @NotNull → es obligatoria
    // @Future → debe ser una fecha futura — no pasada
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDate fechaInicio;

    // Fecha de fin del arriendo
    // @Future → tambien debe ser futura
    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDate fechaFin;

    // Mensaje opcional del arrendatario al arrendador
    // No tiene validacion porque es opcional
    // Ejemplo: "Somos pareja sin mascotas"
    private String mensajeSolicitud;
}