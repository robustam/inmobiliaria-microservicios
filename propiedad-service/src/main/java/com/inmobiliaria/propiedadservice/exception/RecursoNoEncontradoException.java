package com.inmobiliaria.propiedadservice.exception; // Paquete de excepciones personalizadas

// ============================================================
// EXCEPCIÓN: RECURSO NO ENCONTRADO (HTTP 404)
// ============================================================
// Se lanza cuando se busca una propiedad por ID y no existe en la BD.
// GlobalExceptionHandler la captura y retorna HTTP 404 con JSON.
//
// Ejemplo de uso en el service:
//   throw new RecursoNoEncontradoException("Propiedad no encontrada con id: 5");
//
// Respuesta al cliente:
//   { "codigo": "NOT_FOUND", "mensaje": "Propiedad no encontrada con id: 5", ... }
// ============================================================

// extends RuntimeException: excepción NO comprobada (unchecked).
// No obliga a usar try-catch donde se lanza (a diferencia de Exception).
// Spring puede capturarla automáticamente con @ExceptionHandler.
public class RecursoNoEncontradoException extends RuntimeException {

    // Constructor que recibe el mensaje descriptivo del error.
    // super(mensaje): pasa el mensaje a RuntimeException para que sea
    // accesible con e.getMessage() en el GlobalExceptionHandler.
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}