
package com.inmobiliaria.busquedaservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ¿Para que sirve este DTO?
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
    // Deben coincidir exactamente para que Feign
    // pueda convertir el JSON correctamente
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

    // Campo clave para filtrar disponibles
    // true = se puede reservar
    // false = ya tiene reserva activa
    private Boolean disponible;
}