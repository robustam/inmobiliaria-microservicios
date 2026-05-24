package com.inmobiliaria.busquedaservice.exception;

// Excepción para recursos no encontrados (HTTP 404) en busqueda-service.
// GlobalExceptionHandler la captura y retorna JSON estructurado.
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) { super(mensaje); }
}