package com.inmobiliaria.busquedaservice.exception;

// Excepción para reglas de negocio violadas (HTTP 400) en busqueda-service.
// GlobalExceptionHandler la captura y retorna JSON estructurado.
public class NegocioException extends RuntimeException {
    public NegocioException(String mensaje) { super(mensaje); }
}