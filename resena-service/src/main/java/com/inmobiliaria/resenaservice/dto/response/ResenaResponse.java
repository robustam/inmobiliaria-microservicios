package com.inmobiliaria.resenaservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// ¿Para que sirve este DTO?
// Define exactamente que datos devuelve el servidor
// cuando el cliente consulta una resena
// Nunca devolvemos la entidad Resena directamente
// Con este DTO controlamos que informacion exponemos
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResenaResponse {

    // ID unico de la resena generado por MySQL
    private Long id;

    // ID de la propiedad resenada
    private Long propiedadId;

    // ID del arrendatario que escribio la resena
    private Long arrendatarioId;

    // Puntuacion del 1 al 5
    private Integer puntuacion;

    // Comentario escrito por el arrendatario
    private String comentario;

    // Fecha y hora en que se creo la resena
    private LocalDateTime fechaCreacion;
}
