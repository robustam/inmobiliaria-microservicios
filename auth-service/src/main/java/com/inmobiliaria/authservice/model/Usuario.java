package com.inmobiliaria.authservice.model; // Paquete del modelo de datos

// ============================================================
// ENTIDAD: USUARIO (Auth Service)
// ============================================================
// Representa la tabla "auth_usuarios" en la base de datos auth_db.
// Guarda las credenciales de acceso al sistema (username, password).
// NO es el perfil completo del usuario (eso está en usuario-service).
//
// Tabla en MySQL: auth_usuarios
// ============================================================

import jakarta.persistence.*;         // Anotaciones JPA para mapear a base de datos
import jakarta.validation.constraints.*; // Anotaciones de validación (@NotBlank, @Email, etc.)
import lombok.*;                       // Genera código repetitivo automáticamente
import java.time.LocalDateTime;        // Tipo de dato para fecha y hora

// ── Anotaciones de JPA ──────────────────────────────────────
// @Entity: le dice a Hibernate/JPA que esta clase es una ENTIDAD,
// es decir, que corresponde a una tabla en la base de datos.
@Entity

// @Table(name = "auth_usuarios"): especifica el nombre EXACTO de la tabla
// en MySQL. Si se omite, JPA usa el nombre de la clase en minúsculas.
@Table(name = "auth_usuarios")

// ── Anotaciones de Lombok ────────────────────────────────────
// Lombok genera automáticamente el código que no queremos escribir a mano.

// @Data: genera automáticamente getters, setters, toString(), equals() y hashCode()
// para todos los campos. Sin esto tendríamos que escribir ~50 líneas extras.
@Data

// @Builder: habilita el patrón Builder para crear objetos con sintaxis fluente.
// Ejemplo: Usuario.builder().username("juan").email("j@j.cl").build()
// Más legible que usar el constructor con muchos parámetros.
@Builder

// @NoArgsConstructor: genera un constructor vacío (sin parámetros).
// JPA lo necesita obligatoriamente para crear instancias al leer de la BD.
@NoArgsConstructor

// @AllArgsConstructor: genera un constructor con TODOS los campos como parámetros.
// Lo necesita @Builder internamente.
@AllArgsConstructor
public class Usuario {

    // ── Enum interno: Roles del sistema ─────────────────────
    // Enum = tipo especial con valores fijos predefinidos.
    // Integrado aquí como inner class para no crear un archivo separado.
    // Un usuario solo puede tener UNO de estos tres roles.
    public enum Role {
        USER,        // Arrendatario: puede buscar y reservar propiedades
        ADMIN,       // Administrador: acceso total al sistema
        PROPIETARIO  // Dueño: puede publicar y gestionar sus propiedades
    }

    // ── Campos de la entidad (= columnas en la tabla) ────────

    // @Id: marca este campo como la CLAVE PRIMARIA de la tabla.
    // Es el identificador único de cada registro.
    @Id

    // @GeneratedValue(strategy = IDENTITY): el valor se genera automáticamente
    // por MySQL usando AUTO_INCREMENT (1, 2, 3, 4...).
    // No necesitamos asignar el ID manualmente al crear un usuario.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Long = número entero grande (puede llegar a 9 quintillones)

    // @NotBlank: validación que impide que el campo sea null, vacío ("") o solo espacios.
    // El mensaje se muestra cuando falla la validación con @Valid en el Controller.
    @NotBlank(message = "El username es obligatorio")

    // @Column(unique = true, nullable = false):
    //   unique = true   → no puede haber dos usuarios con el mismo username
    //   nullable = false → en la BD, esta columna NO puede ser NULL
    @Column(unique = true, nullable = false)
    private String username; // nombre de usuario para iniciar sesión

    @NotBlank(message = "El email es obligatorio")

    // @Email: valida que el texto tenga formato de email (contiene @ y dominio).
    @Email(message = "Formato de email inválido")
    @Column(unique = true, nullable = false)
    private String email; // correo electrónico único por usuario

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password; // contraseña HASHEADA con BCrypt (nunca en texto plano)

    private String nombre; // nombre real del usuario (opcional)

    // @Enumerated(EnumType.STRING): guarda el valor del enum como texto en la BD
    // (guarda "USER", "ADMIN" o "PROPIETARIO"), NO como número (0, 1, 2).
    // STRING es preferible porque si cambia el orden del enum, los datos siguen correctos.
    @Enumerated(EnumType.STRING)

    // @Builder.Default: cuando se usa Builder, si no se especifica el rol,
    // el valor por defecto será Role.USER.
    @Builder.Default
    private Role role = Role.USER; // por defecto todo usuario nuevo es USER

    private LocalDateTime createdAt; // fecha y hora en que se creó el registro

    // @PrePersist: este método se ejecuta automáticamente ANTES de que JPA
    // guarde el objeto en la base de datos por primera vez (INSERT).
    // Permite asignar valores automáticos sin que el usuario los ingrese.
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(); // registra la fecha/hora actual del servidor
        if (role == null) role = Role.USER; // garantiza que siempre haya un rol asignado
    }
}