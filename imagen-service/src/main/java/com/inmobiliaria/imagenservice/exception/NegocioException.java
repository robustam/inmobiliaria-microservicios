package com.inmobiliaria.imagenservice.exception;

// Excepción para reglas de negocio violadas (HTTP 400) en imagen-service.
// Ejemplo: intentar subir una imagen con datos inválidos.
// GlobalExceptionHandler la captura y retorna JSON estructurado.
public class NegocioException extends RuntimeException {
    public NegocioException(String mensaje) { super(mensaje); }
}