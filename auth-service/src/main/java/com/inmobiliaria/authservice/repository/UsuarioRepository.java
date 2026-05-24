package com.inmobiliaria.authservice.repository; // Paquete de acceso a datos

// ============================================================
// REPOSITORIO: USUARIO (Auth Service)
// ============================================================
// El repositorio es la CAPA DE ACCESO A DATOS (Data Access Layer).
// Es la única clase del sistema que habla directamente con MySQL.
//
// Al extender CrudRepository, Spring Data JPA genera AUTOMÁTICAMENTE
// las consultas SQL para las operaciones básicas sin escribir SQL:
//   - save(entity)       → INSERT o UPDATE
//   - findById(id)       → SELECT * FROM auth_usuarios WHERE id = ?
//   - findAll()          → SELECT * FROM auth_usuarios
//   - deleteById(id)     → DELETE FROM auth_usuarios WHERE id = ?
//   - existsById(id)     → SELECT COUNT(*) FROM auth_usuarios WHERE id = ?
//   - count()            → SELECT COUNT(*) FROM auth_usuarios
//
// Además podemos declarar métodos con nombres especiales y Spring
// genera el SQL automáticamente según la convención de nombres.
// ============================================================

import com.inmobiliaria.authservice.model.Usuario;
import org.springframework.data.repository.CrudRepository; // Interfaz base con operaciones CRUD
import java.util.List;   // Lista de Java
import java.util.Optional; // Contenedor que puede o no tener un valor (evita NullPointerException)

// CrudRepository<Usuario, Long>:
//   Usuario = tipo de entidad que maneja este repositorio
//   Long    = tipo de la clave primaria (el campo @Id)
//
// Al ser una interface, Spring crea la implementación en tiempo de ejecución.
// No necesitamos escribir el código de conexión a la BD.
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    // ── Métodos personalizados ──────────────────────────────
    // Spring Data JPA interpreta el nombre del método y genera el SQL.
    // Convención: findBy + NombreCampo + Condición

    // findByUsername(username) genera:
    // SELECT * FROM auth_usuarios WHERE username = ? LIMIT 1
    // Retorna Optional porque el usuario PUEDE no existir (no lanza error).
    Optional<Usuario> findByUsername(String username);

    // findByEmail(email) genera:
    // SELECT * FROM auth_usuarios WHERE email = ? LIMIT 1
    Optional<Usuario> findByEmail(String email);

    // existsByUsername(username) genera:
    // SELECT COUNT(*) > 0 FROM auth_usuarios WHERE username = ?
    // Retorna true si existe, false si no. Más eficiente que findBy + isPresent().
    boolean existsByUsername(String username);

    // existsByEmail(email) genera:
    // SELECT COUNT(*) > 0 FROM auth_usuarios WHERE email = ?
    boolean existsByEmail(String email);
}