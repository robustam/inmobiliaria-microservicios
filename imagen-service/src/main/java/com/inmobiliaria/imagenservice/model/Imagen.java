package com.inmobiliaria.imagenservice.model; // Paquete del modelo de datos

// ============================================================
// ENTIDAD: IMAGEN (Imagen Service)
// ============================================================
// Representa la tabla "imagenes" en la base de datos imagen_db.
// Gestiona las imágenes (fotos) de las propiedades del sistema.
//
// Cada propiedad puede tener múltiples imágenes.
// Una de ellas puede ser marcada como "principal" (la foto de portada).
//
// IMPORTANTE: este servicio guarda METADATOS de la imagen (URL, nombre,
// tamaño), NO el archivo binario en sí. Las imágenes reales estarían
// en un servidor de archivos o servicio como AWS S3.
//
// Tabla en MySQL: imagenes
// ============================================================

import jakarta.persistence.*;            // Anotaciones JPA
import jakarta.validation.constraints.*; // @NotNull, @NotBlank, @Positive, @Size
import lombok.*;                          // @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
import java.time.LocalDateTime;           // Fecha y hora

@Entity
@Table(name = "imagenes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incremento MySQL
    private Long id;

    // propiedadId: qué propiedad tiene esta imagen.
    // Referencia a propiedad-service (sin @ManyToOne por arquitectura de microservicios).
    @NotNull(message = "El ID de la propiedad es obligatorio")
    @Column(nullable = false)
    private Long propiedadId;

    // url: dirección donde está alojada la imagen.
    // Ejemplo: "https://mi-servidor.cl/fotos/casa-ñuñoa-1.jpg"
    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Column(nullable = false)
    private String url;

    private String nombre;    // nombre del archivo (ej: "sala-comedor.jpg")
    private String tipoMime;  // tipo MIME del archivo (ej: "image/jpeg", "image/png")

    // tamanioBytes: peso del archivo en bytes.
    // @Positive: debe ser mayor que 0.
    @Positive(message = "El tamaño en bytes debe ser positivo")
    private Long tamanioBytes; // ejemplo: 1024000 = 1 MB

    // principal: indica si esta es la foto de portada de la propiedad.
    // Solo puede haber UNA imagen principal por propiedad.
    // Si se establece una nueva como principal, la anterior pierde ese estado.
    @Builder.Default
    private boolean principal = false; // por defecto no es la foto principal

    // descripcion: texto alternativo o descripción de la imagen (accesibilidad).
    // @Size(max = 500): máximo 500 caracteres.
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion; // ej: "Vista de la sala de estar con ventanas grandes"

    private LocalDateTime createdAt; // fecha en que se subió la imagen

    // @PrePersist: asigna la fecha automáticamente antes del INSERT.
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}