package com.inmobiliaria.usuarioservice.dto.rresponse;

// ¿Por que tenemos un DTO de respuesta separado?
// Porque no queremos devolver la entidad Usuario directamente
// La entidad tiene datos internos que el cliente no necesita ver
// Con este DTO controlamos EXACTAMENTE que datos enviamos

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// @Data → genera getters, setters, toString y equals
// @AllArgsConstructor → constructor con TODOS los atributos
//   Ejemplo: new UsuarioResponse(1L, "12345678-9", "Juan", ...)
// @NoArgsConstructor → constructor VACIO
//   Ejemplo: new UsuarioResponse()
//   Spring lo necesita para convertir objetos a JSON
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UsuarioResponse {

    // ID unico del usuario en la base de datos
    // Lo incluimos para que el cliente pueda referenciar al usuario
    private Long id;

    // RUT del usuario — identificador unico chileno
    private String rut;

    // Nombre y apellido del usuario
    private String nombre;
    private String apellido;

    // Email del usuario
    private String email;

    // Telefono de contacto — puede ser null si no lo ingreso
    private String telefono;

    // URL de la foto de perfil — puede ser null si no la ingreso
    private String fotoUrl;

    // Estado del usuario — true = activo, false = desactivado
    // Lo incluimos para que el cliente sepa si puede operar
    private Boolean activo;



}
