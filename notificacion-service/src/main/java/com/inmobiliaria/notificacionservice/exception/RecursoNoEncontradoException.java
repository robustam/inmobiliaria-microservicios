package com.inmobiliaria.notificacionservice.exception;

// Se lanza cuando se busca una notificación por ID y no existe en la BD.
// GlobalExceptionHandler la captura y retorna HTTP 404 con JSON.
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) { super(mensaje); }
}