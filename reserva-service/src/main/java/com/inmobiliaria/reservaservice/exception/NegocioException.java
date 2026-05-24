package com.inmobiliaria.reservaservice.exception;

// Se lanza cuando se viola una regla de negocio en reserva-service.
// Ejemplo: fechaFin anterior a fechaInicio, propiedad no disponible.
// GlobalExceptionHandler la captura y retorna HTTP 400 con JSON.
public class NegocioException extends RuntimeException {
    public NegocioException(String mensaje) { super(mensaje); }
}