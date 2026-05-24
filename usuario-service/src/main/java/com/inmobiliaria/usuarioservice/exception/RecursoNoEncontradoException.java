package com.inmobiliaria.usuarioservice.exception; // Paquete de excepciones

// ============================================================
// EXCEPCIÓN: RECURSO NO ENCONTRADO (HTTP 404)
// ============================================================
// Se lanza cuando se busca un usuario por ID o email y no existe.
// GlobalExceptionHandler la captura y retorna HTTP 404 con JSON.
//
// Ejemplo:
//   throw new RecursoNoEncontradoException("Usuario no encontrado con id: 5");
// ============================================================

// extends RuntimeException: excepción no comprobada (no requiere try-catch).
public class RecursoNoEncontradoException extends RuntimeException {
    // super(mensaje): pasa el mensaje a RuntimeException para e.getMessage().
    public RecursoNoEncontradoException(String mensaje) { super(mensaje); }
}