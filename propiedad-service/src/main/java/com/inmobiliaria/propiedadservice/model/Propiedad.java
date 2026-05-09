
package com.inmobiliaria.propiedadservice.model;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ¿Para que sirve este paquete model?
// Contiene las entidades JPA — son clases que representan
// tablas en la base de datos MySQL
// Cada atributo de la clase = una columna en la tabla
// Spring/Hibernate crea la tabla automaticamente

@Entity
@Table(name = "propiedades")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Propiedad {

    // Clave primaria — MySQL genera el numero automaticamente
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del arrendador — referencia al usuario dueno
    // No es una relacion JPA porque el usuario
    // vive en otro microservicio (usuario-service)
    // Solo guardamos el ID como referencia
    @Column(nullable = false)
    private Long arrendadorId;

    // Titulo de la propiedad
    // Ejemplo: "Departamento 2D en Providencia"
    @Column(nullable = false)
    private String titulo;

    // Descripcion detallada de la propiedad
    // TEXT permite textos largos en MySQL
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Direccion exacta de la propiedad
    @Column(nullable = false)
    private String direccion;

    // Comuna donde esta la propiedad
    // Ejemplo: "Providencia", "Las Condes"
    @Column(nullable = false)
    private String comuna;

    // Ciudad donde esta la propiedad
    // Ejemplo: "Santiago"
    @Column(nullable = false)
    private String ciudad;

    // Precio mensual del arriendo en pesos chilenos
    // DECIMAL(10,2) → hasta 10 digitos con 2 decimales
    // Ejemplo: 450000.00
    @Column(nullable = false)
    private Double precioMensual;

    // Numero de habitaciones de la propiedad
    @Column(nullable = false)
    private Integer habitaciones;

    // Numero de banos de la propiedad
    @Column(nullable = false)
    private Integer banios;

    // Metros cuadrados de la propiedad
    @Column(nullable = false)
    private Double m2;

    // true = disponible para arrendar
    // false = ya arrendada o no disponible
    @Column(nullable = false)
    private Boolean disponible = true;
}