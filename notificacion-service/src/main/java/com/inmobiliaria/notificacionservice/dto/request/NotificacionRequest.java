
package com.inmobiliaria.notificacionservice.dto.request;

import com.inmobiliaria.notificacionservice.model.Notificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// ¿Para que sirve este DTO?
// Define exactamente que datos necesita recibir
// el servicio para crear una notificacion
// Lo usan otros microservicios internamente
// Ejemplo: reserva-service llama a notificacion-service
// cuando una reserva es aprobada
@Data
public class NotificacionRequest {

    // ID del usuario que recibira la notificacion
    // @NotNull porque es un numero no un texto
    @NotNull(message = "El id del usuario es obligatorio")
    private Long usuarioId;

    // Tipo de notificacion
    // Define el motivo del aviso
    // Debe ser uno de los valores del enum Tipo
    @NotNull(message = "El tipo de notificacion es obligatorio")
    private Notificacion.Tipo tipo;

    // Mensaje descriptivo que vera el usuario
    // @NotBlank porque es texto obligatorio
    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;
}