package com.inmobiliaria.usuarioservice.service; // Paquete de servicios

// ============================================================
// SERVICIO: USUARIO - LÓGICA DE NEGOCIO
// ============================================================
// Gestiona los perfiles de usuario del sistema inmobiliario.
// Incluye arrendatarios y propietarios.
//
// Responsabilidades:
//   - Listar usuarios activos
//   - Buscar por ID, email o ciudad
//   - Crear nuevos perfiles de usuario
//   - Actualizar datos del perfil
//   - Desactivar usuarios (borrado lógico: activo = false)
// ============================================================

import com.inmobiliaria.usuarioservice.exception.GlobalExceptionHandler.NegocioException;             // Error 400
import com.inmobiliaria.usuarioservice.exception.GlobalExceptionHandler.RecursoNoEncontradoException; // Error 404
import com.inmobiliaria.usuarioservice.model.Usuario;                          // Entidad
import com.inmobiliaria.usuarioservice.repository.UsuarioRepository;           // Acceso BD
import lombok.RequiredArgsConstructor; // Genera constructor para inyección
import lombok.extern.slf4j.Slf4j;     // Activa el logger log.*
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    // Acceso a la tabla "usuarios" en usuario_db.
    private final UsuarioRepository usuarioRepository;

    // Retorna la lista de todos los usuarios con activo = true.
    // Los usuarios desactivados (borrado lógico) no aparecen.
    public List<Usuario> findAll() {
        log.debug("Obteniendo todos los usuarios activos");
        return usuarioRepository.findByActivoTrue();
    }

    // Busca un usuario por su ID. Lanza HTTP 404 si no existe.
    public Usuario findById(Long id) {
        log.debug("Buscando usuario con id: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + id));
    }

    // Busca un usuario por su email. Lanza HTTP 404 si no existe.
    // Útil para verificar datos de un arrendatario al procesar una reserva.
    public Usuario findByEmail(String email) {
        log.debug("Buscando usuario con email: {}", email);
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email: " + email));
    }

    // Retorna usuarios activos de una ciudad específica.
    // "IgnoreCase" = "Santiago" = "santiago" = "SANTIAGO"
    public List<Usuario> findByCiudad(String ciudad) {
        log.debug("Buscando usuarios en ciudad: {}", ciudad);
        return usuarioRepository.findByCiudadIgnoreCaseAndActivoTrue(ciudad);
    }

    // Crea un nuevo perfil de usuario.
    // Verifica que el email no esté ya registrado antes de guardar.
    public Usuario create(Usuario usuario) {
        log.info("Creando usuario con email: {}", usuario.getEmail());
        // Regla de negocio: email único en el sistema.
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new NegocioException("El email ya está registrado");
        }
        // save() ejecuta INSERT. @PrePersist asigna createdAt y updatedAt.
        return usuarioRepository.save(usuario);
    }

    // Actualiza el perfil de un usuario existente.
    // Solo actualiza los campos que vienen con valor (los null se ignoran).
    public Usuario update(Long id, Usuario datos) {
        log.info("Actualizando usuario con id: {}", id);
        Usuario usuario = findById(id); // lanza 404 si no existe

        // nombre es obligatorio, siempre se actualiza.
        usuario.setNombre(datos.getNombre());
        // Campos opcionales: solo se actualizan si el request trae un valor.
        if (datos.getApellido() != null) usuario.setApellido(datos.getApellido());
        if (datos.getTelefono() != null) usuario.setTelefono(datos.getTelefono());
        if (datos.getDireccion() != null) usuario.setDireccion(datos.getDireccion());
        if (datos.getCiudad() != null) usuario.setCiudad(datos.getCiudad());

        // save() en entidad con ID ejecuta UPDATE. @PreUpdate actualiza updatedAt.
        return usuarioRepository.save(usuario);
    }

    // Desactiva un usuario (borrado lógico).
    // No borra el registro de la BD para conservar historial de reservas y reseñas.
    public void delete(Long id) {
        log.info("Desactivando usuario con id: {}", id);
        Usuario usuario = findById(id); // lanza 404 si no existe
        usuario.setActivo(false);       // cambia la bandera a inactivo
        usuarioRepository.save(usuario); // guarda el cambio en BD
    }
}