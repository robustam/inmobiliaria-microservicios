package com.inmobiliaria.usuarioservice.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// ¿Que hace @RestControllerAdvice?
// Es como un GUARDIA que vigila todos los controllers
// Cuando ocurre un error en cualquier controller
// esta clase lo captura y devuelve una respuesta JSON ordenada
// Sin esto Spring devolveria un error feo con mucho texto tecnico
// Con esto devolvemos un JSON claro y entendible al cliente
@RestControllerAdvice

public class GlobalExceptionHandler {

    // Logger para registrar todos los errores que ocurran
    // Nos ayuda a saber que fallo y cuando fallo
    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ¿Que es @ExceptionHandler?
    // Le dice a Spring: cuando ocurra ESTE tipo de error
    // ejecuta ESTE metodo para manejarlo

    // Este metodo captura errores de validacion
    // Ocurre cuando el cliente envia datos incorrectos
    // Ejemplo: nombre vacio, email sin @, rut con formato malo
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        // Registramos el error en la consola de IntelliJ
        log.warn("Error de validacion en usuario-service: {}",
                ex.getMessage());

        // Map es como un diccionario — guarda pares clave:valor
        // Ejemplo: { "nombre": "El nombre es obligatorio" }
        Map<String, String> errores = new HashMap<>();

        // getFieldErrors() retorna lista de todos los campos
        // que fallaron la validacion
        // forEach recorre cada error y lo agrega al mapa
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.put(
                        // e.getField() → nombre del campo que fallo
                        // Ejemplo: "nombre", "email", "rut"
                        e.getField(),
                        // e.getDefaultMessage() → mensaje del error
                        // Ejemplo: "El nombre es obligatorio"
                        e.getDefaultMessage()
                ));

        // Retornamos 400 Bad Request con los errores detallados
        // El cliente sabe exactamente que campo fallo y por que
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errores);
    }

    // Este metodo captura errores de logica de negocio
    // Son errores que lanzamos manualmente con throw
    // Ejemplo: "El email ya esta registrado"
    // Ejemplo: "Usuario no encontrado"
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(
            RuntimeException ex) {

        // Registramos el error como ERROR en la consola
        log.error("Error de negocio en usuario-service: {}",
                ex.getMessage());

        // Creamos el mapa con el mensaje de error
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        // Retornamos 400 Bad Request con el mensaje de error
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    // Este metodo captura CUALQUIER otro error no controlado
    // Es el ultimo recurso — captura errores inesperados
    // Ejemplo: fallo de base de datos, error de memoria, etc
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(
            Exception ex) {

        // Registramos el error grave en la consola
        log.error("Error inesperado en usuario-service: {}",
                ex.getMessage());

        // Creamos mensaje generico — no exponemos detalles tecnicos
        // por seguridad al cliente
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor");

        // Retornamos 500 Internal Server Error
        // Este codigo significa que el problema es del servidor
        // no del cliente
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }


}
