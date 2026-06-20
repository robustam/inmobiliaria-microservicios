package com.inmobiliaria.authservice.exception; // Paquete de excepciones

// ============================================================
// MANEJADOR GLOBAL DE EXCEPCIONES - AUTH SERVICE
// ============================================================
// Este componente captura AUTOMÁTICAMENTE todas las excepciones
// que ocurran en los Controllers y las convierte en respuestas
// HTTP con formato JSON estructurado.
//
// Sin esto, si ocurre un error Spring respondería con HTML o
// un JSON genérico poco informativo. Con esto, SIEMPRE
// responde con:
// {
//   "codigo":    "NOT_FOUND",
//   "mensaje":   "Usuario no encontrado con id: 5",
//   "timestamp": "2025-05-22T10:30:00"
// }
// ============================================================

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; // Excepción de @Valid
import org.springframework.web.bind.annotation.ExceptionHandler;    // Marca qué excepción maneja
import org.springframework.web.bind.annotation.RestControllerAdvice; // Activa el manejador global
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors; // Para unir mensajes de validación

// @RestControllerAdvice: Le dice a Spring que esta clase intercepta
// excepciones de TODOS los @RestController de este microservicio.
// Es como un "vigilante" que captura errores antes de que lleguen al cliente.
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler(X.class): indica que este método se ejecuta
    // automáticamente cuando se lanza una excepción de tipo X.

    // Captura RecursoNoEncontradoException → HTTP 404 Not Found
    // Ocurre cuando un ID no existe en la base de datos.
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(RecursoNoEncontradoException e) {
        // ResponseEntity.status(404) = código HTTP 404
        // .body(...) = cuerpo de la respuesta en formato JSON
        return ResponseEntity.status(404).body(errorBody("NOT_FOUND", e.getMessage()));
    }

    // Captura NegocioException → HTTP 400 Bad Request
    // Ocurre cuando se viola una regla de negocio (ej: email duplicado).
    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Map<String, Object>> handleNegocio(NegocioException e) {
        return ResponseEntity.status(400).body(errorBody("BAD_REQUEST", e.getMessage()));
    }

    // Captura MethodArgumentNotValidException → HTTP 400
    // Ocurre cuando falla la validación de @Valid en el Controller
    // (ej: campo @NotBlank vacío, @Email con formato incorrecto).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        // e.getBindingResult().getFieldErrors() = lista de todos los errores de campo
        // Para cada error: "nombreCampo: mensaje del error"
        // Collectors.joining(", ") une todos los mensajes separados por coma
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(400).body(errorBody("VALIDATION_ERROR", mensaje));
    }

    // Captura IllegalArgumentException → HTTP 400
    // Ocurre cuando se pasa un valor inválido a métodos como Enum.valueOf()
    // (ej: estado "INVALIDO" que no existe en el enum EstadoPropiedad).
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(400).body(errorBody("BAD_REQUEST", e.getMessage()));
    }

    // Captura cualquier otra excepción no prevista → HTTP 500 Internal Server Error
    // Es el "catch-all" para errores inesperados. El mensaje al cliente es
    // genérico para no exponer detalles internos del sistema.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception e) {
        log.error("Error inesperado: {}", e.getMessage(), e);
        return ResponseEntity.status(500).body(errorBody("SERVER_ERROR", "Error interno del servidor"));
    }

    // Método auxiliar que construye el Map (objeto JSON) de respuesta de error.
    // Map.of() crea un mapa inmutable con pares clave-valor.
    // Este mapa se serializa automáticamente a JSON por Jackson (incluido en Spring Boot).
    private Map<String, Object> errorBody(String codigo, String mensaje) {
        return Map.of(
            "codigo",    codigo,                          // código de error legible
            "mensaje",   mensaje,                         // descripción del error
            "timestamp", LocalDateTime.now().toString()   // fecha y hora exacta del error
        );
    }

    public static class NegocioException extends RuntimeException {
        public NegocioException(String mensaje) { super(mensaje); }
    }

    public static class RecursoNoEncontradoException extends RuntimeException {
        public RecursoNoEncontradoException(String mensaje) { super(mensaje); }
    }
}