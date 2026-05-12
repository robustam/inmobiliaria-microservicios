
package com.inmobiliaria.imagenservice.model;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// ¿Para que sirve este paquete model?
// Representa la tabla imagenes en MySQL
// Guarda informacion sobre las imagenes subidas
// No guarda el archivo en si — guarda la URL
// donde esta almacenada la imagen
@Entity
@Table(name = "imagenes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Imagen {

    // Clave primaria con AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID de la entidad duena de la imagen
    // Puede ser el id de una propiedad o de un usuario
    // Ejemplo: propiedad con id 5 tiene 3 imagenes
    @Column(nullable = false)
    private Long entidadId;

    // Tipo de entidad que posee la imagen
    // Usa enum para limitar los valores posibles
    // PROPIEDAD = imagen de una propiedad
    // USUARIO   = foto de perfil de un usuario
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEntidad tipoEntidad;

    // URL donde esta almacenada la imagen
    // Ejemplo: "https://storage.com/imagen1.jpg"
    // O ruta local: "/uploads/propiedad_5_foto1.jpg"
    @Column(nullable = false)
    private String url;

    // Nombre original del archivo subido
    // Ejemplo: "foto_salon.jpg"
    @Column(nullable = false)
    private String nombre;

    // Fecha y hora en que se subio la imagen
    @Column(nullable = false)
    private LocalDateTime fechaCreacion =
            LocalDateTime.now();

    // Tipos de entidad que pueden tener imagenes
    public enum TipoEntidad {
        PROPIEDAD,  // imagen de una propiedad
        USUARIO     // foto de perfil de usuario
    }
}