package com.inmobiliaria.usuarioservice.model; // Paquete del modelo de datos

// ============================================================
// ENTIDAD: USUARIO (Usuario Service)
// ============================================================
// Representa la tabla "usuarios" en la base de datos usuario_db.
// Guarda el PERFIL COMPLETO del usuario (nombre, email, teléfono, ciudad).
//
// DIFERENCIA con auth-service:
//   auth-service/Usuario → guarda credenciales (username + password hash)
//   usuario-service/Usuario → guarda perfil (nombre, email, teléfono)
//
// Tabla en MySQL: usuarios
// ============================================================

import jakarta.persistence.*;            // @Entity, @Table, @Id, @Column, @PrePersist, @PreUpdate
import jakarta.validation.constraints.*; // @NotBlank, @Email, @Size
import lombok.*;                          // @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
import java.time.LocalDateTime;           // Tipo para fechas con hora

@Entity
@Table(name = "usuarios")    // nombre de la tabla en MySQL
@Data                        // genera getters, setters, toString, equals, hashCode
@Builder                     // patrón Builder para crear objetos: Usuario.builder().nombre("Juan").build()
@NoArgsConstructor           // constructor vacío para JPA
@AllArgsConstructor          // constructor con todos los campos para @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incremento en MySQL
    private Long id; // clave primaria

    // nombre: obligatorio, no puede estar vacío.
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false) // en MySQL: columna NOT NULL
    private String nombre; // nombre de pila del usuario

    private String apellido; // apellido (opcional)

    // email: obligatorio, único, con formato válido de correo electrónico.
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido") // valida que tenga @ y dominio
    @Column(unique = true, nullable = false) // único en la tabla + NOT NULL
    private String email; // correo electrónico (es el identificador de contacto)

    // @Size(min, max): valida la longitud del String.
    // Teléfonos chilenos: ej. "+56912345678" (12 chars) o "912345678" (9 chars)
    @Size(min = 9, max = 15, message = "El teléfono debe tener entre 9 y 15 caracteres")
    private String telefono; // número de teléfono (opcional)

    private String direccion; // dirección postal (opcional)
    private String ciudad;    // ciudad de residencia (opcional), ej: "Santiago", "Valparaíso"

    // activo: flag de borrado lógico.
    // true = usuario activo y visible en el sistema.
    // false = "eliminado" (no aparece en consultas normales, pero el registro existe).
    // @Builder.Default: valor por defecto al usar el Builder.
    @Builder.Default
    private boolean activo = true; // todos los usuarios nuevos empiezan activos

    private LocalDateTime createdAt; // fecha de creación del perfil
    private LocalDateTime updatedAt; // fecha de la última modificación

    // @PrePersist: se ejecuta antes del primer INSERT.
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(); // asigna la fecha actual al crear
        updatedAt = LocalDateTime.now(); // igual al crear, cambia con cada update
    }

    // @PreUpdate: se ejecuta antes de cada UPDATE.
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(); // registra cuándo fue la última modificación
    }
}