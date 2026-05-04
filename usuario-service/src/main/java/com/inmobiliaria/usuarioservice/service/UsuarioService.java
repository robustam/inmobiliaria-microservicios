package com.inmobiliaria.usuarioservice.service;

import com.inmobiliaria.usuarioservice.dto.request.UsuarioRequest;
import com.inmobiliaria.usuarioservice.dto.rresponse.UsuarioResponse;
import com.inmobiliaria.usuarioservice.model.Usuario;
import com.inmobiliaria.usuarioservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// @Service le dice a Spring que esta clase contiene
// la logica de negocio del microservicio
// Es la capa del COCINERO — aqui se procesan los datos
// El controller recibe el pedido y se lo pasa al service
// El service lo procesa y retorna el resultado
@Service

// @RequiredArgsConstructor genera automaticamente el constructor
// con todos los atributos que tienen la palabra final
// Esto se llama inyeccion de dependencias por constructor
// Es la forma correcta de inyectar dependencias en Spring
@RequiredArgsConstructor

public class UsuarioService {

    // Logger para registrar todas las operaciones importantes
    // Nos permite saber que paso, cuando paso y si hubo errores
    private static final Logger log =
            LoggerFactory.getLogger(UsuarioService.class);

    // Repository para acceder a la base de datos
    // final significa que no puede cambiar despues de crearse
    // Spring lo inyecta automaticamente gracias a @RequiredArgsConstructor
    private final UsuarioRepository usuarioRepository;

    // ─────────────────────────────────────────────────
    // METODO: Crear un nuevo usuario
    // Recibe: UsuarioRequest con los datos del cliente
    // Retorna: UsuarioResponse con los datos guardados
    // ─────────────────────────────────────────────────
    public UsuarioResponse crearUsuario(UsuarioRequest request) {

        // Registramos que se inicio la creacion de un usuario
        log.info("Iniciando creacion de usuario con email: {}",
                request.getEmail());

        try {
            // Verificamos que el email no este ya registrado
            // Si existe lanzamos error que captura GlobalExceptionHandler
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                log.warn("Email ya registrado: {}", request.getEmail());
                throw new RuntimeException(
                        "El email ya esta registrado");
            }

            // Verificamos que el rut no este ya registrado
            if (usuarioRepository.existsByRut(request.getRut())) {
                log.warn("RUT ya registrado: {}", request.getRut());
                throw new RuntimeException(
                        "El RUT ya esta registrado");
            }

            // Creamos el objeto Usuario con los datos del request
            // new Usuario() crea un objeto vacio
            // los setters rellenan cada campo
            Usuario usuario = new Usuario();
            usuario.setRut(request.getRut());
            usuario.setNombre(request.getNombre());
            usuario.setApellido(request.getApellido());
            usuario.setEmail(request.getEmail());
            usuario.setTelefono(request.getTelefono());
            usuario.setFotoUrl(request.getFotoUrl());

            // Por defecto el usuario nace activo
            usuario.setActivo(true);

            // Guardamos el usuario en MySQL
            // save() genera: INSERT INTO usuarios VALUES (...)
            // Retorna el usuario con el id generado por MySQL
            Usuario guardado = usuarioRepository.save(usuario);

            log.info("Usuario creado exitosamente con id: {}",
                    guardado.getId());

            // Convertimos la entidad a DTO de respuesta
            // No devolvemos la entidad directamente por seguridad
            return convertirAResponse(guardado);

        } catch (RuntimeException e) {
            // Relanzamos errores de negocio al GlobalExceptionHandler
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear usuario: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al crear el usuario");
        }
    }

    // ─────────────────────────────────────────────────
    // METODO: Obtener un usuario por su ID
    // Recibe: id del usuario
    // Retorna: UsuarioResponse con los datos del usuario
    // ─────────────────────────────────────────────────
    public UsuarioResponse obtenerUsuario(Long id) {

        log.info("Buscando usuario con id: {}", id);

        // findById busca el usuario por su id en MySQL
        // orElseThrow lanza excepcion si no encuentra nada
        // Es mas limpio que hacer if(usuario == null)
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con id: {}", id);
                    return new RuntimeException(
                            "Usuario no encontrado");
                });

        log.info("Usuario encontrado: {}", usuario.getEmail());
        return convertirAResponse(usuario);
    }

    // ─────────────────────────────────────────────────
    // METODO: Listar todos los usuarios
    // Retorna: Lista de UsuarioResponse
    // ─────────────────────────────────────────────────
    public List<UsuarioResponse> listarUsuarios() {

        log.info("Listando todos los usuarios");

        // findAll() genera: SELECT * FROM usuarios
        // Retorna una lista de entidades Usuario
        List<Usuario> usuarios = usuarioRepository.findAll();

        log.info("Total de usuarios encontrados: {}",
                usuarios.size());

        // stream() permite procesar la lista elemento por elemento
        // map() convierte cada Usuario en UsuarioResponse
        // collect() junta todos los resultados en una nueva lista
        return usuarios.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────
    // METODO: Actualizar un usuario existente
    // Recibe: id del usuario y nuevos datos
    // Retorna: UsuarioResponse con los datos actualizados
    // ─────────────────────────────────────────────────
    public UsuarioResponse actualizarUsuario(
            Long id, UsuarioRequest request) {

        log.info("Actualizando usuario con id: {}", id);

        // Buscamos el usuario — si no existe lanza excepcion
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con id: {}", id);
                    return new RuntimeException(
                            "Usuario no encontrado");
                });

        // Actualizamos solo los campos que vienen en el request
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setTelefono(request.getTelefono());
        usuario.setFotoUrl(request.getFotoUrl());

        // No actualizamos email ni rut porque son identificadores
        // unicos que no deben cambiar

        // save() con un objeto que ya tiene id genera:
        // UPDATE usuarios SET ... WHERE id = ?
        Usuario actualizado = usuarioRepository.save(usuario);

        log.info("Usuario actualizado exitosamente: {}",
                actualizado.getId());

        return convertirAResponse(actualizado);
    }

    // ─────────────────────────────────────────────────
    // METODO: Eliminar un usuario
    // Recibe: id del usuario
    // ─────────────────────────────────────────────────
    public void eliminarUsuario(Long id) {

        log.info("Eliminando usuario con id: {}", id);

        // Verificamos que el usuario exista antes de eliminar
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con id: {}", id);
                    return new RuntimeException(
                            "Usuario no encontrado");
                });

        // deleteById genera: DELETE FROM usuarios WHERE id = ?
        usuarioRepository.deleteById(id);

        log.info("Usuario eliminado exitosamente: {}",
                usuario.getEmail());
    }

    // ─────────────────────────────────────────────────
    // METODO PRIVADO: Convertir entidad a DTO
    // Este metodo es privado — solo lo usa esta clase
    // Convierte un objeto Usuario en UsuarioResponse
    // Lo usamos para no repetir el mismo codigo en cada metodo
    // ─────────────────────────────────────────────────
    private UsuarioResponse convertirAResponse(Usuario usuario) {

        // Creamos el DTO de respuesta con todos los datos
        // new UsuarioResponse(param1, param2, ...) usa el
        // constructor con todos los parametros que genero
        // @AllArgsConstructor de Lombok
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getRut(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getFotoUrl(),
                usuario.getActivo()
        );
    }



}
