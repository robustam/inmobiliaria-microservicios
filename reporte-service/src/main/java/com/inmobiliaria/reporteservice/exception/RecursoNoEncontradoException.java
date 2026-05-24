package com.inmobiliaria.reporteservice.exception;

// Se lanza cuando se busca un reporte por ID y no existe en el historial.
// GlobalExceptionHandler la captura y retorna HTTP 404 con JSON.
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) { super(mensaje); }
}