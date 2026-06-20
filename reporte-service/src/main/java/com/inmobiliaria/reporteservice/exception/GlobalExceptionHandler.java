package com.inmobiliaria.reporteservice.exception;

// ============================================================
// MANEJADOR GLOBAL DE EXCEPCIONES - REPORTE SERVICE
// ============================================================
// Intercepta excepciones de los Controllers y retorna JSON estructurado.
// ============================================================

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // RecursoNoEncontradoException → HTTP 404 (reporte no existe en historial)
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(RecursoNoEncontradoException e) {
        return ResponseEntity.status(404).body(errorBody("NOT_FOUND", e.getMessage()));
    }

    // NegocioException → HTTP 400
    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Map<String, Object>> handleNegocio(NegocioException e) {
        return ResponseEntity.status(400).body(errorBody("BAD_REQUEST", e.getMessage()));
    }

    // MethodArgumentNotValidException → HTTP 400 (falla de @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(400).body(errorBody("VALIDATION_ERROR", mensaje));
    }

    // IllegalArgumentException → HTTP 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(400).body(errorBody("BAD_REQUEST", e.getMessage()));
    }

    // Exception.class → HTTP 500 (error inesperado, incluye errores de Feign)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception e) {
        log.error("Error inesperado: {}", e.getMessage(), e);
        return ResponseEntity.status(500).body(errorBody("SERVER_ERROR", "Error interno del servidor"));
    }

    private Map<String, Object> errorBody(String codigo, String mensaje) {
        return Map.of("codigo", codigo, "mensaje", mensaje, "timestamp", LocalDateTime.now().toString());
    }

    public static class NegocioException extends RuntimeException {
        public NegocioException(String mensaje) { super(mensaje); }
    }

    public static class RecursoNoEncontradoException extends RuntimeException {
        public RecursoNoEncontradoException(String mensaje) { super(mensaje); }
    }
}