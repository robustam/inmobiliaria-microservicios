package com.inmobiliaria.resenaservice.exception;

// Se lanza cuando se viola una regla de negocio en resena-service.
// Ejemplo: calificación fuera del rango 1-5.
// GlobalExceptionHandler la captura y retorna HTTP 400 con JSON.
public class NegocioException extends RuntimeException {
    public NegocioException(String mensaje) { super(mensaje); }
}