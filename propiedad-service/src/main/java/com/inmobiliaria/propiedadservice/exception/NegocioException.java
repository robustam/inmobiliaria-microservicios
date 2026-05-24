package com.inmobiliaria.propiedadservice.exception; // Paquete de excepciones personalizadas

// ============================================================
// EXCEPCIÓN: REGLA DE NEGOCIO VIOLADA (HTTP 400)
// ============================================================
// Se lanza cuando una operación viola una regla de negocio.
// Es diferente a RecursoNoEncontradoException (que es para 404).
//
// Ejemplos de uso en propiedad-service:
//   - Intentar reservar una propiedad INACTIVA
//   - Cambiar a un estado inválido
//
// GlobalExceptionHandler la captura y retorna HTTP 400 con JSON.
//
// Respuesta al cliente:
//   { "codigo": "BAD_REQUEST", "mensaje": "descripción del error", ... }
// ============================================================

// extends RuntimeException: excepción no comprobada, no requiere try-catch.
public class NegocioException extends RuntimeException {

    // super(mensaje): pasa el mensaje a la clase padre RuntimeException.
    public NegocioException(String mensaje) {
        super(mensaje);
    }
}