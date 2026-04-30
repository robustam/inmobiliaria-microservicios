package com.inmobiliaria.authservice.controller;

import com.inmobiliaria.authservice.dto.request.LoginRequest;
import com.inmobiliaria.authservice.dto.request.RegisterRequest;
import com.inmobiliaria.authservice.dto.response.AuthResponse;
import com.inmobiliaria.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    // Logger para registrar cada peticion que llega
    // y cada respuesta que sale del controller
    private static final Logger log =
            LoggerFactory.getLogger(AuthController.class);

    // Servicio que contiene toda la logica de negocio
    // El controller solo delega — no hace logica
    private final AuthService authService;

    // POST /api/v1/auth/register
    // Endpoint para registrar un nuevo usuario
    // Recibe: { rut, email, password, rol }
    // Retorna: { token, rut, email, rol, mensaje }
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            // @Valid activa las validaciones del DTO
            // Si algo falla, lanza MethodArgumentNotValidException
            // que captura el GlobalExceptionHandler
            @Valid @RequestBody RegisterRequest request) {

        // Registra en consola que llego una peticion
        log.info("REQUEST register → email: {}",
                request.getEmail());

        // Delega toda la logica al service
        // El controller no sabe como se registra — solo pide que se haga
        AuthResponse response = authService.register(request);

        // Registra en consola que se envio una respuesta
        log.info("RESPONSE register → status: 201 CREATED");

        // 201 Created es el codigo HTTP correcto
        // cuando se crea un nuevo recurso exitosamente
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // POST /api/v1/auth/login
    // Endpoint para autenticar un usuario existente
    // Recibe: { email, password }
    // Retorna: { token, rut, email, rol, mensaje }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            // @Valid valida que email y password no vengan vacios
            // @RequestBody convierte el JSON a objeto LoginRequest
            @Valid @RequestBody LoginRequest request) {

        // Registra el intento de login en consola
        log.info("REQUEST login → email: {}",
                request.getEmail());

        // Delega la autenticacion al service
        // Si las credenciales son incorrectas, el service lanza excepcion
        AuthResponse response = authService.login(request);

        // Registra la respuesta exitosa
        log.info("RESPONSE login → status: 200 OK");

        // 200 OK es el codigo correcto para login exitoso
        return ResponseEntity.ok(response);
    }

    // GET /api/v1/auth/validate?token=xxxxx
    // Endpoint para validar si un token JWT es valido
    // Lo usa el API Gateway para verificar autenticacion
    // antes de dejar pasar la peticion al microservicio destino
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(
            // @RequestParam lee el parametro de la URL
            // Ejemplo: /validate?token=eyJhbGci...
            @RequestParam String token) {

        // Registra que se esta validando un token
        log.info("REQUEST validate token");

        // Pregunta al service si el token es valido
        // Retorna true si es valido, false si no lo es
        boolean valido = authService.validateToken(token);

        // Retorna 200 OK con true o false en el body
        return ResponseEntity.ok(valido);
    }
}
