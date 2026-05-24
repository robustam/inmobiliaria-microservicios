package com.inmobiliaria.reservaservice.exception;

// ============================================================
// MANEJADOR GLOBAL DE EXCEPCIONES - RESERVA SERVICE
// ============================================================
// Intercepta excepciones de los Controllers y retorna JSON estructurado.
// Evita que errores internos lleguen al cliente sin formato.
// ============================================================

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; // falla de @Valid
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

// @RestControllerAdvice: intercepta errores de todos los @RestController de este servicio.
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // RecursoNoEncontradoException → HTTP 404 (reserva no existe)
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(RecursoNoEncontradoException e) {
        return ResponseEntity.status(404).body(errorBody("NOT_FOUND", e.getMessage()));
    }

    // NegocioException → HTTP 400 (regla de negocio violada)
    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Map<String, Object>> handleNegocio(NegocioException e) {
        return ResponseEntity.status(400).body(errorBody("BAD_REQUEST", e.getMessage()));
    }

    // MethodArgumentNotValidException → HTTP 400 (falla de @Valid en el Controller)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(400).body(errorBody("VALIDATION_ERROR", mensaje));
    }

    // IllegalArgumentException → HTTP 400 (valor inválido para EstadoReserva.valueOf())
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(400).body(errorBody("BAD_REQUEST", e.getMessage()));
    }

    // Exception.class → HTTP 500 (cualquier error no previsto)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception e) {
        log.error("Error inesperado: {}", e.getMessage(), e);
        return ResponseEntity.status(500).body(errorBody("SERVER_ERROR", "Error interno del servidor"));
    }

    // Construye el mapa JSON de respuesta de error estándar.
    private Map<String, Object> errorBody(String codigo, String mensaje) {
        return Map.of("codigo", codigo, "mensaje", mensaje, "timestamp", LocalDateTime.now().toString());
    }
}