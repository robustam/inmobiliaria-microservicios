
package com.inmobiliaria.imagenservice.dto.request;

import com.inmobiliaria.imagenservice.model.Imagen;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// ¿Para que sirve este DTO?
// Define exactamente que datos debe enviar el cliente
// para registrar una imagen en el sistema
// El cliente sube la imagen y envia la URL resultante
@Data
public class ImagenRequest {

    // ID de la entidad duena de la imagen
    // Ejemplo: id de la propiedad o del usuario
    @NotNull(message = "El id de la entidad es obligatorio")
    private Long entidadId;

    // Tipo de entidad que posee la imagen
    // Debe ser PROPIEDAD o USUARIO
    @NotNull(message = "El tipo de entidad es obligatorio")
    private Imagen.TipoEntidad tipoEntidad;

    // URL donde esta almacenada la imagen
    // Ejemplo: "https://storage.com/imagen1.jpg"
    @NotBlank(message = "La url de la imagen es obligatoria")
    private String url;

    // Nombre original del archivo
    // Ejemplo: "foto_salon.jpg"
    @NotBlank(message = "El nombre de la imagen es obligatorio")
    private String nombre;
}