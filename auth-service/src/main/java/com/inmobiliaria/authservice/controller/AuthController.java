package com.inmobiliaria.authservice.controller; // Paquete del controlador

// ============================================================
// CONTROLADOR HTTP - AUTH SERVICE
// ============================================================
// El Controller es la PUERTA DE ENTRADA del microservicio.
// Recibe peticiones HTTP del API Gateway o de clientes externos
// y las delega al Service (la capa de lógica de negocio).
//
// Endpoints expuestos (todos bajo /api/v1/auth):
//   GET  /health     → verificar que el servicio está vivo
//   POST /login      → iniciar sesión y obtener token JWT
//   POST /register   → registrar nuevo usuario
//   GET  /validate   → verificar si un token JWT es válido
//
// El Controller NO tiene lógica de negocio:
//   - Recibe el request
//   - Llama al service
//   - Retorna el response
// ============================================================

import com.inmobiliaria.authservice.dto.LoginResponse;            // DTO de respuesta de login
import com.inmobiliaria.authservice.dto.RegisterRequest;          // DTO de entrada para registro
import com.inmobiliaria.authservice.dto.TokenValidationResponse;  // DTO de respuesta de validación
import com.inmobiliaria.authservice.service.AuthService;          // Servicio con la lógica
import jakarta.validation.Valid;          // Activa las validaciones de los DTOs (@NotBlank, etc.)
import lombok.RequiredArgsConstructor;    // Genera constructor con campos final
import org.springframework.http.ResponseEntity; // Contenedor de respuesta HTTP con código de estado
import org.springframework.web.bind.annotation.*; // Todas las anotaciones HTTP del Controller

// @RestController: combina @Controller + @ResponseBody.
//   @Controller: marca la clase como controlador Spring MVC
//   @ResponseBody: los métodos retornan datos (JSON) en lugar de vistas HTML
// Es la anotación estándar para APIs REST en Spring Boot.
@RestController

// @RequestMapping("/api/v1/auth"): prefijo de URL para TODOS los endpoints de este controller.
// Todos los métodos heredan esta ruta base.
// Ejemplo: /api/v1/auth + /login = /api/v1/auth/login
@RequestMapping("/api/v1/auth")

// @RequiredArgsConstructor: genera constructor con todos los campos final para inyección.
@RequiredArgsConstructor
public class AuthController {

    // AuthService: inyectado por constructor (patrón preferido sobre @Autowired).
    // Contiene toda la lógica de login, registro y validación.
    private final AuthService authService;

    // ────────────────────────────────────────────────────────────
    // GET /api/v1/auth/health
    // ────────────────────────────────────────────────────────────
    // Endpoint de salud: verifica que el servicio está corriendo.
    // Usado por herramientas de monitoreo y para depuración rápida.
    // Retorna un String simple (no JSON).
    @GetMapping("/health")
    public String health() {
        return "Auth Service is UP! ✅";
    }

    // ────────────────────────────────────────────────────────────
    // POST /api/v1/auth/login
    // ────────────────────────────────────────────────────────────
    // Inicia sesión con username y password.
    // Retorna un token JWT si las credenciales son correctas.
    //
    // Body de ejemplo:
    //   { "username": "juan123", "password": "miPass123" }
    //
    // Respuesta exitosa (HTTP 200):
    //   { "token": "eyJhbGc...", "username": "juan123", "role": "USER", "mensaje": "Login exitoso" }
    //
    // Error (HTTP 400): credenciales incorrectas
    // Error (HTTP 404): usuario no encontrado
    // ────────────────────────────────────────────────────────────

    // @PostMapping("/login"): maneja peticiones HTTP POST a /api/v1/auth/login
    @PostMapping("/login")
    // ResponseEntity<LoginResponse>: wrapper que incluye el cuerpo (LoginResponse) + código HTTP.
    // Permite retornar diferentes códigos según el resultado (200, 201, 400, 404, etc.)
    public ResponseEntity<LoginResponse> login(
            // @RequestBody: deserializa el JSON del body HTTP al objeto LoginRequest.
            // Jackson (incluido en Spring Boot) hace la conversión automáticamente.
            @RequestBody LoginRequest request) {
        // ResponseEntity.ok(): retorna HTTP 200 con el body indicado.
        return ResponseEntity.ok(authService.login(request.getUsername(), request.getPassword()));
    }

    // ────────────────────────────────────────────────────────────
    // POST /api/v1/auth/register
    // ────────────────────────────────────────────────────────────
    // Registra un nuevo usuario en el sistema.
    // Retorna el token JWT del nuevo usuario (puede empezar a usarlo de inmediato).
    //
    // Body de ejemplo:
    //   {
    //     "username": "juan123",
    //     "email": "juan@mail.cl",
    //     "password": "miPass123",
    //     "nombre": "Juan Pérez",
    //     "role": "USER"
    //   }
    //
    // Respuesta exitosa (HTTP 201 Created):
    //   { "token": "eyJhbGc...", "username": "juan123", "role": "USER", "mensaje": "Registro exitoso" }
    // ────────────────────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(
            // @Valid: activa la validación del DTO RegisterRequest.
            // Si algún campo no cumple sus anotaciones (@NotBlank, @Email, etc.),
            // Spring lanza MethodArgumentNotValidException → GlobalExceptionHandler → HTTP 400.
            @Valid @RequestBody RegisterRequest request) {
        // ResponseEntity.status(201): retorna HTTP 201 Created (estándar para recursos nuevos).
        // .body(resultado): incluye el DTO de respuesta como JSON.
        return ResponseEntity.status(201).body(authService.register(request));
    }

    // ────────────────────────────────────────────────────────────
    // GET /api/v1/auth/validate?token=eyJhbGc...
    // ────────────────────────────────────────────────────────────
    // Valida un token JWT y retorna si es válido y qué usuario representa.
    // Usado internamente por el API Gateway para verificar peticiones.
    //
    // Parámetro de query: ?token=... (en la URL)
    //
    // Respuesta exitosa (HTTP 200):
    //   { "valid": true, "username": "juan123", "role": "USER", "mensaje": "Token válido" }
    // ────────────────────────────────────────────────────────────
    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validate(
            // @RequestParam: extrae el parámetro "token" de la URL.
            // Ejemplo URL: /api/v1/auth/validate?token=eyJhbGc...
            @RequestParam String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }

    // ────────────────────────────────────────────────────────────
    // CLASE INTERNA: LoginRequest
    // ────────────────────────────────────────────────────────────
    // DTO (Data Transfer Object) para recibir las credenciales de login.
    // Se define aquí como clase estática interna para no crear un archivo separado,
    // ya que solo se usa en este controller.
    //
    // "static": no necesita una instancia del Controller para existir.
    // ────────────────────────────────────────────────────────────
    static class LoginRequest {
        private String username; // nombre de usuario
        private String password; // contraseña en texto plano

        // Getters y Setters escritos manualmente (no usamos Lombok aquí).
        // Jackson los necesita para serializar/deserializar el JSON.
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}