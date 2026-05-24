package com.inmobiliaria.usuarioservice.controller; // Paquete del controlador

// ============================================================
// CONTROLADOR HTTP: USUARIO SERVICE
// ============================================================
// Endpoints para gestionar perfiles de usuario.
//
// Endpoints (todos bajo /api/v1/usuarios):
//   GET    /health          → verificar que el servicio está vivo
//   GET    /                → listar todos los usuarios activos
//   GET    /{id}            → obtener usuario por ID
//   GET    /email/{email}   → buscar usuario por email
//   GET    /ciudad/{ciudad} → listar usuarios de una ciudad
//   POST   /                → crear nuevo usuario
//   PUT    /{id}            → actualizar usuario
//   DELETE /{id}            → desactivar usuario (borrado lógico)
// ============================================================

import com.inmobiliaria.usuarioservice.model.Usuario;        // Entidad
import com.inmobiliaria.usuarioservice.service.UsuarioService; // Lógica de negocio
import jakarta.validation.Valid;          // Activa validaciones (@NotBlank, @Email, @Size)
import lombok.RequiredArgsConstructor;    // Constructor para inyección
import org.springframework.http.ResponseEntity; // Respuesta con código HTTP
import org.springframework.web.bind.annotation.*; // Anotaciones HTTP
import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios") // prefijo de URL para todos los endpoints
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // GET /api/v1/usuarios/health → verificación de que el servicio está vivo
    @GetMapping("/health")
    public String health() {
        return "Usuario Service is UP! ✅";
    }

    // GET /api/v1/usuarios → lista todos los usuarios con activo = true
    @GetMapping
    public ResponseEntity<List<Usuario>> getUsuarios() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    // GET /api/v1/usuarios/{id} → busca un usuario por su ID
    // HTTP 404 si no existe (manejado por GlobalExceptionHandler)
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    // GET /api/v1/usuarios/email/{email} → busca usuario por email
    // Útil cuando otros servicios necesitan datos del usuario con solo el email
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.findByEmail(email));
    }

    // GET /api/v1/usuarios/ciudad/{ciudad} → lista usuarios activos de una ciudad
    // Ejemplo: /api/v1/usuarios/ciudad/Santiago
    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<Usuario>> getByCiudad(@PathVariable String ciudad) {
        return ResponseEntity.ok(usuarioService.findByCiudad(ciudad));
    }

    // POST /api/v1/usuarios → crea un nuevo usuario
    // @Valid: ejecuta las validaciones de la entidad (@NotBlank, @Email, @Size)
    // HTTP 201 Created al crear exitosamente
    @PostMapping
    public ResponseEntity<Usuario> create(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.status(201).body(usuarioService.create(usuario));
    }

    // PUT /api/v1/usuarios/{id} → actualiza los datos de un usuario existente
    // @Valid: valida los campos del body recibido
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable Long id,
                                          @Valid @RequestBody Usuario datos) {
        return ResponseEntity.ok(usuarioService.update(id, datos));
    }

    // DELETE /api/v1/usuarios/{id} → desactiva el usuario (borrado lógico)
    // No borra el registro; cambia activo = false
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }
}