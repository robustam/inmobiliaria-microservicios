package com.inmobiliaria.authservice.exception; // Paquete de excepciones del auth-service

// ============================================================
// EXCEPCIÓN: REGLA DE NEGOCIO VIOLADA (Error 400)
// ============================================================
// Esta clase representa errores de lógica de negocio.
// Se lanza cuando el dato existe pero la OPERACIÓN no está
// permitida según las reglas del sistema.
//
// Ejemplos de cuándo usarla:
//   - Intentar registrar un username que ya existe
//   - Intentar arrendar una propiedad que ya está arrendada
//   - Ingresar una calificación fuera del rango 1-5
//   - Credenciales de login incorrectas
//
// Al lanzarla, el GlobalExceptionHandler responde con HTTP 400.
// ============================================================

public class NegocioException extends RuntimeException {

    // Constructor: recibe el mensaje descriptivo del error de negocio.
    // Ejemplo: new NegocioException("El email ya está registrado")
    public NegocioException(String mensaje) {
        super(mensaje); // pasa el mensaje a la superclase RuntimeException
    }
}