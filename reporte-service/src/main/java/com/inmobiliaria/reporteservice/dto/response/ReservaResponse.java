
package com.inmobiliaria.reporteservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

// ¿Para que sirve este DTO?
// Es una copia del ReservaResponse del reserva-service
// Cuando Feign llama a reserva-service y recibe la respuesta
// necesita convertir ese JSON a un objeto Java
// Este DTO es ese objeto — debe tener los mismos campos
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaResponse {

    // Todos los campos que devuelve reserva-service
    // Deben coincidir exactamente para que Feign
    // pueda convertir el JSON correctamente
    private Long id;
    private Long propiedadId;
    private Long arrendatarioId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    // Estado de la reserva
    // PENDIENTE, APROBADA, RECHAZADA, CANCELADA
    private String estado;

    private String mensajeSolicitud;
    private LocalDateTime fechaCreacion;
}