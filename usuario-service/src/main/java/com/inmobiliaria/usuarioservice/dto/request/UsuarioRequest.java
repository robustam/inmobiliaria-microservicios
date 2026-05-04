package com.inmobiliaria.usuarioservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
// ¿Que es un DTO?
// DTO significa Data Transfer Object (Objeto de Transferencia de Datos)
// Es un objeto que define EXACTAMENTE que datos puede enviar el cliente
// Separamos el DTO de la entidad por seguridad y control
// El cliente no puede enviar campos que no queremos que envie

// @Data de Lombok genera automaticamente:
// - getters→ metodos para leer los atributos (getNombre(), etc)
// - setters  → metodos para modificar los atributos (setNombre(), etc)
// - toString → representacion en texto del objeto
// - equals   → para comparar dos objetos
@Data
public class UsuarioRequest {

    // @NotBlank significa que el campo:
    // - No puede ser null (vacio absoluto)
    // - No puede ser "" (texto vacio)
    // - No puede ser "   " (solo espacios)
    // Si viene vacio Spring retorna error 400 con el mensaje definido

    // @Pattern valida que el texto cumpla un formato especifico
    // regexp = expresion regular que define el formato valido
    // Este patron acepta: 12345678-9 o 1234567-K
    @NotBlank(message = "El rut es obligatorio")
    @Pattern(
            regexp = "^[0-9]{7,8}-[0-9Kk]$",
            message = "Formato de rut invalido. Ejemplo: 12345678-9"
    )
    private String rut;

    // @Size define el largo minimo y maximo del texto
    // min = 2 → al menos 2 caracteres
    // max = 50 → maximo 50 caracteres
    @NotBlank(message = "El nombre es obligatorio")
    @Size(
            min = 2,
            max = 50,
            message = "El nombre debe tener entre 2 y 50 caracteres"
    )
    private String nombre;

    // Apellido con las mismas reglas que el nombre
    @NotBlank(message = "El apellido es obligatorio")
    @Size(
            min = 2,
            max = 50,
            message = "El apellido debe tener entre 2 y 50 caracteres"
    )
    private String apellido;

    // @Email valida que el texto tenga formato de email
    // Ejemplo valido:   usuario@correo.com
    // Ejemplo invalido: usuariocorreo.com (sin @)
    @Email(message = "El email no tiene formato valido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    // Telefono es opcional — no tiene @NotBlank
    // El cliente puede enviarlo o no enviarlo
    // @Pattern valida que solo tenga numeros y el signo +
    @Pattern(
            regexp = "^[+]?[0-9]{8,15}$",
            message = "Formato de telefono invalido"
    )
    private String telefono;

    // URL de la foto — tambien es opcional
    // El cliente puede enviarlo o no enviarlo
    private String fotoUrl;

}
