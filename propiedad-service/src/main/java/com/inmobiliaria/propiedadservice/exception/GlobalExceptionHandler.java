

package com.inmobiliaria.propiedadservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// ¿Para que sirve el paquete exception?
// Captura todos los errores del microservicio
// Devuelve respuestas JSON ordenadas en vez de errores tecnicos
// @RestControllerAdvice vigila todos los controllers
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Captura errores de validacion @Valid
    // Ejemplo: titulo vacio, precio negativo
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        log.warn("Error de validacion en propiedad-service: {}",
                ex.getMessage());

        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.put(
                        e.getField(),
                        e.getDefaultMessage()
                ));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errores);
    }

    // Captura errores de negocio lanzados manualmente
    // Ejemplo: propiedad no encontrada
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(
            RuntimeException ex) {

        log.error("Error de negocio en propiedad-service: {}",
                ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    // Captura cualquier otro error inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(
            Exception ex) {

        log.error("Error inesperado en propiedad-service: {}",
                ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}