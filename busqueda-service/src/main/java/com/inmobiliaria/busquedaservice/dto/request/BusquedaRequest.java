
package com.inmobiliaria.busquedaservice.dto.request;

import lombok.Data;

// ¿Para que sirve este DTO?
// Define los filtros que puede enviar el cliente
// para buscar propiedades
// Todos los campos son opcionales — el cliente
// puede filtrar por uno o varios a la vez
// Ejemplo: buscar en Santiago con precio menor a 500000
@Data
public class BusquedaRequest {

    // Filtro por comuna
    // Ejemplo: "Providencia", "Las Condes"
    // Si viene null no filtra por comuna
    private String comuna;

    // Filtro por ciudad
    // Ejemplo: "Santiago", "Valparaiso"
    // Si viene null no filtra por ciudad
    private String ciudad;

    // Filtro por precio minimo
    // Solo muestra propiedades con precio >= precioMin
    // Si viene null no filtra por precio minimo
    private Double precioMin;

    // Filtro por precio maximo
    // Solo muestra propiedades con precio <= precioMax
    // Si viene null no filtra por precio maximo
    private Double precioMax;

    // Filtro por numero de habitaciones
    // Solo muestra propiedades con ese numero de habitaciones
    // Si viene null no filtra por habitaciones
    private Integer habitaciones;

    // Filtro por metros cuadrados minimos
    // Solo muestra propiedades con m2 >= m2Min
    // Si viene null no filtra por m2
    private Double m2Min;

    // Filtro solo disponibles
    // true = solo muestra propiedades disponibles
    // false = muestra todas
    // Por defecto true — el cliente busca para reservar
    private Boolean soloDisponibles = true;
}