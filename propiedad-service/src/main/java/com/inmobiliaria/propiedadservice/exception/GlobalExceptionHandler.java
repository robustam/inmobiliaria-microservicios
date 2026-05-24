package com.inmobiliaria.propiedadservice.exception; // Paquete de excepciones

// ============================================================
// MANEJADOR GLOBAL DE EXCEPCIONES - PROPIEDAD SERVICE
// ============================================================
// Captura automáticamente todas las excepciones lanzadas en los
// Controllers y las convierte en respuestas JSON estructuradas.
//
// Sin este componente, los errores llegarían al cliente como HTML
// o como un JSON genérico poco informativo de Spring.
//
// Con este componente, TODOS los errores retornan:
// {
//   "codigo":    "NOT_FOUND",
//   "mensaje":   "Propiedad no encontrada con id: 5",
//   "timestamp": "2025-05-22T10:30:00"
// }
// ============================================================

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; // Excepción de @Valid
import org.springframework.web.bind.annotation.ExceptionHandler;    // Qué excepción maneja
import org.springframework.web.bind.annotation.RestControllerAdvice; // Activa el manejador global
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors; // Para unir mensajes de validación

// @RestControllerAdvice: intercepta excepciones de TODOS los @RestController
// de este microservicio antes de que lleguen al cliente.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // RecursoNoEncontradoException → HTTP 404 Not Found
    // Ocurre cuando se busca una propiedad por ID y no existe.
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(RecursoNoEncontradoException e) {
        return ResponseEntity.status(404).body(errorBody("NOT_FOUND", e.getMessage()));
    }

    // NegocioException → HTTP 400 Bad Request
    // Ocurre cuando se viola una regla de negocio.
    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Map<String, Object>> handleNegocio(NegocioException e) {
        return ResponseEntity.status(400).body(errorBody("BAD_REQUEST", e.getMessage()));
    }

    // MethodArgumentNotValidException → HTTP 400
    // Ocurre cuando @Valid falla en el Controller (campos @NotBlank, etc. no cumplen).
    // Recopila TODOS los errores de validación y los une en un mensaje.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        // getFieldErrors(): lista de campos que fallaron la validación.
        // Ejemplo: "titulo: El título es obligatorio, precio: El precio debe ser mayor a cero"
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(400).body(errorBody("VALIDATION_ERROR", mensaje));
    }

    // IllegalArgumentException → HTTP 400
    // Ocurre cuando TipoPropiedad.valueOf("INVALIDO") falla (valor no existe en el enum).
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(400).body(errorBody("BAD_REQUEST", e.getMessage()));
    }

    // Exception.class → HTTP 500 Internal Server Error
    // "catch-all": captura cualquier error no previsto.
    // Mensaje genérico para no exponer detalles internos al cliente.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception e) {
        return ResponseEntity.status(500).body(errorBody("SERVER_ERROR", "Error interno del servidor"));
    }

    // Método auxiliar: construye el Map que Jackson serializa como JSON.
    // Map.of(): crea un mapa inmutable con los pares clave-valor indicados.
    private Map<String, Object> errorBody(String codigo, String mensaje) {
        return Map.of(
            "codigo",    codigo,                          // código de error legible
            "mensaje",   mensaje,                         // descripción del error
            "timestamp", LocalDateTime.now().toString()   // fecha y hora del error
        );
    }
}