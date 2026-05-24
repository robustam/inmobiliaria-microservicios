package com.inmobiliaria.authservice.exception; // Paquete de excepciones del auth-service

// ============================================================
// EXCEPCIÓN: RECURSO NO ENCONTRADO (Error 404)
// ============================================================
// Esta clase representa el error que ocurre cuando se busca
// algo en la base de datos y NO existe.
// Ejemplo: buscar usuario con ID 999 que no existe en la BD.
//
// Al lanzarla (throw), el GlobalExceptionHandler la captura
// y responde automáticamente con HTTP 404 + mensaje de error.
//
// Patrón: todas las excepciones del proyecto extienden
// RuntimeException para que no sea necesario declararlas
// en la firma de los métodos con "throws".
// ============================================================

// RuntimeException: excepción que NO necesita ser declarada ni capturada
// obligatoriamente. Es "no chequeada" (unchecked exception).
public class RecursoNoEncontradoException extends RuntimeException {

    // Constructor que recibe el mensaje de error.
    // super(mensaje) llama al constructor de RuntimeException
    // pasándole el mensaje, que luego se recupera con e.getMessage().
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje); // guarda el mensaje de error en la superclase
    }
}