package com.inmobiliaria.usuarioservice.repository; // Paquete de acceso a datos

// ============================================================
// REPOSITORIO: USUARIO (Usuario Service)
// ============================================================
// Capa de acceso a la tabla "usuarios" en usuario_db.
// CrudRepository provee automáticamente las operaciones CRUD básicas.
// Los métodos personalizados son generados por Spring Data JPA
// interpretando el nombre del método.
// ============================================================

import com.inmobiliaria.usuarioservice.model.Usuario; // Entidad que maneja este repositorio
import org.springframework.data.repository.CrudRepository; // Interfaz base con CRUD
import java.util.List;    // Para retornar listas de usuarios
import java.util.Optional; // Contenedor que puede o no tener valor (evita NullPointerException)

// CrudRepository<Usuario, Long>:
//   Usuario = entidad asociada
//   Long    = tipo de la clave primaria (@Id)
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    // findByEmail(): busca un usuario por su email.
    // Retorna Optional porque el usuario puede no existir.
    // SQL generado: SELECT * FROM usuarios WHERE email = ? LIMIT 1
    Optional<Usuario> findByEmail(String email);

    // existsByEmail(): verifica si ya existe un usuario con ese email.
    // SQL: SELECT COUNT(*) > 0 FROM usuarios WHERE email = ?
    // Más eficiente que findByEmail().isPresent() (no trae todos los datos).
    boolean existsByEmail(String email);

    // findByActivoTrue(): retorna solo los usuarios con activo = true.
    // "ActivoTrue" = Spring interpreta: WHERE activo = true
    // Implementa el borrado lógico: los usuarios "eliminados" no aparecen.
    List<Usuario> findByActivoTrue();

    // findByCiudadIgnoreCaseAndActivoTrue():
    //   - CiudadIgnoreCase = WHERE LOWER(ciudad) = LOWER(?) (sin importar mayúsculas)
    //   - AndActivoTrue    = AND activo = true
    // SQL: SELECT * FROM usuarios WHERE LOWER(ciudad) = LOWER(?) AND activo = true
    List<Usuario> findByCiudadIgnoreCaseAndActivoTrue(String ciudad);
}