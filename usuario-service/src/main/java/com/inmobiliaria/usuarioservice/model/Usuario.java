package com.inmobiliaria.usuarioservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// @Entity indica que esta clase será una entidad de la base de datos.
// Es decir, cada objeto Usuario se guardará como un registro en una tabla.
@Entity
// @Table permite indicar el nombre de la tabla en la base de datos.
// En este caso la tabla se llamará "usuarios".
@Table(name = "usuarios")
// @Data es una anotación de Lombok.
// Genera automáticamente:
// getters, setters, toString, equals y hashCode.
@Data
// @AllArgsConstructor crea automáticamente un constructor
// con TODOS los atributos.
@AllArgsConstructor
// @NoArgsConstructor crea automáticamente un constructor vacío.
@NoArgsConstructor
public class Usuario {
    // @Id indica que este atributo será la clave primaria.
    // La clave primaria identifica de manera única cada usuario.
    @Id
    // @GeneratedValue permite que el id se genere automáticamente.
    // IDENTITY hace que la base de datos aumente el id solo.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;

    // @Column sirve para configurar una columna de la tabla.

    // unique = true:
    // No pueden existir dos usuarios con el mismo rut.

    // nullable = false:
    // El rut es obligatorio, no puede quedar vacío.
    @Column(unique = true, nullable = false)
    private String rut;

    // El nombre es obligatorio.
    @Column(nullable = false)
    private  String nombre;
    // El apellido también es obligatorio.
    @Column(nullable = false)
    private  String apellido;
    // El email debe ser único y obligatorio.
    // No pueden existir dos correos iguales.
    @Column(unique = true,nullable = false)
    private  String email;

    // El teléfono es opcional porque no tiene nullable = false.
    @Column
    private  String telefono;
    // Guarda la dirección o URL de la foto del usuario.
    // También es opcional
    @Column
    private  String fotoUrl;
    // Indica si el usuario está activo o no.
    // Por defecto siempre será true.
    @Column(nullable = false)
    private  Boolean activo= true;






}
