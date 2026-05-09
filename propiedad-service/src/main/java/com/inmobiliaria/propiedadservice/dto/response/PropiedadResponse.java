
package com.inmobiliaria.propiedadservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ¿Para que sirve el paquete dto/response?
// Define exactamente que datos devuelve el servidor al cliente
// Nunca devolvemos la entidad directamente
// Con este DTO controlamos que informacion exponemos

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropiedadResponse {

    // Todos los datos de la propiedad que el cliente necesita ver
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

    // Estado de disponibilidad — importante para el cliente
    // true = puede reservar, false = no disponible
    private Boolean disponible;
}