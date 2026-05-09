
package com.inmobiliaria.resenaservice.model;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// ¿Para que sirve este paquete model?
// Representa la tabla resenas en MySQL
// Una resena es la opinion que deja un arrendatario
// sobre una propiedad despues de haberla arrendado
@Entity
@Table(name = "resenas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resena {

    // Clave primaria con AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID de la propiedad que se esta resenando
    // Referencia a propiedad-service
    // No es relacion JPA — vive en otro microservicio
    @Column(nullable = false)
    private Long propiedadId;

    // ID del arrendatario que escribe la resena
    // Referencia a usuario-service
    @Column(nullable = false)
    private Long arrendatarioId;

    // Puntuacion del 1 al 5
    // 1 = muy malo, 5 = excelente
    // Se valida en el DTO con @Min y @Max
    @Column(nullable = false)
    private Integer puntuacion;

    // Comentario escrito por el arrendatario
    // TEXT permite textos largos en MySQL
    @Column(columnDefinition = "TEXT")
    private String comentario;

    // Fecha y hora en que se creo la resena
    @Column(nullable = false)
    private LocalDateTime fechaCreacion =
            LocalDateTime.now();
}
