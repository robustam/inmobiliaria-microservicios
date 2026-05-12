
package com.inmobiliaria.imagenservice.dto.response;

import com.inmobiliaria.imagenservice.model.Imagen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// ¿Para que sirve este DTO?
// Define exactamente que datos devuelve el servidor
// cuando el cliente consulta las imagenes
// Nunca devolvemos la entidad directamente
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImagenResponse {

    // ID unico de la imagen generado por MySQL
    private Long id;

    // ID de la entidad duena de la imagen
    private Long entidadId;

    // Tipo de entidad — PROPIEDAD o USUARIO
    private Imagen.TipoEntidad tipoEntidad;

    // URL donde esta almacenada la imagen
    // El cliente usa esta URL para mostrar la imagen
    private String url;

    // Nombre original del archivo
    private String nombre;

    // Fecha y hora en que se subio la imagen
    private LocalDateTime fechaCreacion;
}