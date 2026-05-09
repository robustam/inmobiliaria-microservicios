
package com.inmobiliaria.resenaservice.dto.request;

import jakarta.validation.constraints.*;
        import lombok.Data;

// ¿Para que sirve este DTO?
// Define exactamente que datos debe enviar el cliente
// para crear una resena
// Tiene validaciones para proteger la base de datos
@Data
public class ResenaRequest {

    // ID de la propiedad que se va a resenar
    // @NotNull porque es un numero no un texto
    @NotNull(message = "El id de la propiedad es obligatorio")
    private Long propiedadId;

    // ID del arrendatario que escribe la resena
    @NotNull(message = "El id del arrendatario es obligatorio")
    private Long arrendatarioId;

    // Puntuacion del 1 al 5
    // @Min valida que sea minimo 1
    // @Max valida que sea maximo 5
    // Si viene 0 o 6 Spring retorna error 400
    @NotNull(message = "La puntuacion es obligatoria")
    @Min(value = 1, message = "La puntuacion minima es 1")
    @Max(value = 5, message = "La puntuacion maxima es 5")
    private Integer puntuacion;

    // Comentario del arrendatario — obligatorio
    // @Size define el largo minimo y maximo
    @NotBlank(message = "El comentario es obligatorio")
    @Size(
            min = 10,
            max = 500,
            message = "El comentario debe tener entre 10 y 500 caracteres"
    )
    private String comentario;
}
