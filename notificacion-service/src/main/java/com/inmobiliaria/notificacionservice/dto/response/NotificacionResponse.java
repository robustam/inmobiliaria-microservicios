package com.inmobiliaria.notificacionservice.dto.response;

import com.inmobiliaria.notificacionservice.model.Notificacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// ¿Para que sirve este DTO?
// Define exactamente que datos devuelve el servidor
// cuando el cliente consulta sus notificaciones
// Nunca devolvemos la entidad directamente
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionResponse {

    // ID unico de la notificacion
    private Long id;

    // ID del usuario que recibio la notificacion
    private Long usuarioId;

    // Tipo de notificacion
    // Ejemplo: RESERVA_APROBADA, NUEVA_RESERVA
    private Notificacion.Tipo tipo;

    // Mensaje descriptivo que ve el usuario
    // Ejemplo: "Tu reserva #5 fue aprobada"
    private String mensaje;

    // Estado de lectura
    // false = nueva sin leer (aparece como no leida)
    // true  = ya fue vista por el usuario
    private Boolean leido;

    // Fecha y hora en que se genero la notificacion
    private LocalDateTime fechaCreacion;
}