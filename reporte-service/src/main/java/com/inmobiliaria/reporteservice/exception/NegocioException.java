package com.inmobiliaria.reporteservice.exception;

// Excepción para reglas de negocio violadas (HTTP 400) en reporte-service.
// GlobalExceptionHandler la captura y retorna JSON estructurado.
public class NegocioException extends RuntimeException {
    public NegocioException(String mensaje) { super(mensaje); }
}