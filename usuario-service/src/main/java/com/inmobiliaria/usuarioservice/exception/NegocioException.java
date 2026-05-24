package com.inmobiliaria.usuarioservice.exception; // Paquete de excepciones

// ============================================================
// EXCEPCIÓN: REGLA DE NEGOCIO VIOLADA (HTTP 400)
// ============================================================
// Se lanza cuando una operación viola una regla de negocio.
// Ejemplo: intentar crear un usuario con un email ya registrado.
// GlobalExceptionHandler la captura y retorna HTTP 400 con JSON.
// ============================================================

public class NegocioException extends RuntimeException {
    public NegocioException(String mensaje) { super(mensaje); }
}