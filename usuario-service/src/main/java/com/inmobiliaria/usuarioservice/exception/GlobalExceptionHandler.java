package com.inmobiliaria.usuarioservice.exception; // Paquete de excepciones

// ============================================================
// MANEJADOR GLOBAL DE EXCEPCIONES - USUARIO SERVICE
// ============================================================
// Intercepta automáticamente todas las excepciones de los Controllers
// y las convierte en respuestas JSON estructuradas con código HTTP correcto.
// ============================================================

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; // Falla de @Valid
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

// @RestControllerAdvice: vigila todos los @RestController de este microservicio.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // RecursoNoEncontradoException → HTTP 404 (usuario no existe)
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(RecursoNoEncontradoException e) {
        return ResponseEntity.status(404).body(errorBody("NOT_FOUND", e.getMessage()));
    }

    // NegocioException → HTTP 400 (regla de negocio violada, ej: email duplicado)
    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Map<String, Object>> handleNegocio(NegocioException e) {
        return ResponseEntity.status(400).body(errorBody("BAD_REQUEST", e.getMessage()));
    }

    // MethodArgumentNotValidException → HTTP 400 (falló @Valid en el Controller)
    // Une todos los mensajes de error de campos en un solo String.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(400).body(errorBody("VALIDATION_ERROR", mensaje));
    }

    // IllegalArgumentException → HTTP 400 (valor inválido para enum u otro tipo)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(400).body(errorBody("BAD_REQUEST", e.getMessage()));
    }

    // Exception.class → HTTP 500 (cualquier error no previsto)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception e) {
        return ResponseEntity.status(500).body(errorBody("SERVER_ERROR", "Error interno del servidor"));
    }

    // Construye el mapa JSON de respuesta de error.
    private Map<String, Object> errorBody(String codigo, String mensaje) {
        return Map.of("codigo", codigo, "mensaje", mensaje, "timestamp", LocalDateTime.now().toString());
    }
}