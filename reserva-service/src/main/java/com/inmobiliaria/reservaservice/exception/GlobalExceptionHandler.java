package com.inmobiliaria.reservaservice.exception;

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
// y devuelve respuestas JSON ordenadas al cliente
// Sin esto Spring devolveria errores tecnicos feos
// @RestControllerAdvice vigila todos los controllers
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Logger para registrar todos los errores
    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Captura errores de validacion @Valid
    // Ejemplo: fecha de inicio en el pasado
    // Ejemplo: propiedadId nulo
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        log.warn("Error de validacion en reserva-service: {}",
                ex.getMessage());

        // Recorre cada campo que fallo y lo agrega al mapa
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.put(
                        e.getField(),
                        e.getDefaultMessage()
                ));

        // 400 Bad Request — datos incorrectos del cliente
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errores);
    }

    // Captura errores de logica de negocio
    // Ejemplo: propiedad no disponible
    // Ejemplo: reserva no encontrada
    // Ejemplo: fechas incorrectas
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(
            RuntimeException ex) {

        log.error("Error de negocio en reserva-service: {}",
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

        log.error("Error inesperado en reserva-service: {}",
                ex.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}