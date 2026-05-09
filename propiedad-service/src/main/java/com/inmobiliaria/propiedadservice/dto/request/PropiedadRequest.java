
package com.inmobiliaria.propiedadservice.dto.request;

import jakarta.validation.constraints.*;
        import lombok.Data;

// ¿Para que sirve el paquete dto/request?
// Define exactamente que datos puede enviar el cliente
// Tiene las validaciones para proteger la base de datos
// Si el cliente envia datos incorrectos Spring retorna error 400

@Data
public class PropiedadRequest {

    // ID del arrendador que publica la propiedad
    // @NotNull porque es un numero — no usamos @NotBlank
    @NotNull(message = "El id del arrendador es obligatorio")
    private Long arrendadorId;

    // Titulo descriptivo de la propiedad
    // @Size limita el largo del texto
    @NotBlank(message = "El titulo es obligatorio")
    @Size(
            min = 10,
            max = 150,
            message = "El titulo debe tener entre 10 y 150 caracteres"
    )
    private String titulo;

    // Descripcion detallada — opcional pero recomendada
    private String descripcion;

    // Direccion exacta — obligatoria
    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;

    // Comuna — obligatoria
    @NotBlank(message = "La comuna es obligatoria")
    private String comuna;

    // Ciudad — obligatoria
    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    // Precio mensual — debe ser mayor a 0
    // @Positive valida que el numero sea positivo
    @NotNull(message = "El precio mensual es obligatorio")
    @Positive(message = "El precio mensual debe ser mayor a 0")
    private Double precioMensual;

    // Habitaciones — minimo 1
    // @Min valida que el numero sea mayor o igual al minimo
    @NotNull(message = "El numero de habitaciones es obligatorio")
    @Min(value = 1, message = "Debe tener al menos 1 habitacion")
    private Integer habitaciones;

    // Banos — minimo 1
    @NotNull(message = "El numero de banios es obligatorio")
    @Min(value = 1, message = "Debe tener al menos 1 banio")
    private Integer banios;

    // Metros cuadrados — debe ser mayor a 0
    @NotNull(message = "Los metros cuadrados son obligatorios")
    @Positive(message = "Los m2 deben ser mayor a 0")
    private Double m2;
}