
package com.inmobiliaria.reservaservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ¿Para que sirve este archivo?
// Es una copia del PropiedadResponse del propiedad-service
// Cuando Feign llama a propiedad-service y recibe la respuesta
// necesita convertir ese JSON a un objeto Java
// Este DTO es ese objeto — debe tener los mismos campos
// que devuelve propiedad-service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropiedadResponse {

    // Todos los campos que devuelve propiedad-service
    // Deben coincidir exactamente con PropiedadResponse
    // del propiedad-service para que Feign pueda convertir
    private Long id;
    private Long arrendadorId;
    private String titulo;
    private String descripcion;
    private String direccion;
    private String comuna;
    private String ciudad;
    private Double precioMensual;
    private Integer habitaciones;
    private Integer banios;
    private Double m2;

    // Este campo es el mas importante para la logica de reservas
    // true = disponible para reservar
    // false = ya tiene una reserva activa
    private Boolean disponible;
}