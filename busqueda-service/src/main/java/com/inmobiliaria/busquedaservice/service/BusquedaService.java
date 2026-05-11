
package com.inmobiliaria.busquedaservice.service;

import com.inmobiliaria.busquedaservice.client.PropiedadClient;
import com.inmobiliaria.busquedaservice.dto.request.BusquedaRequest;
import com.inmobiliaria.busquedaservice.dto.response.PropiedadResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// ¿Para que sirve el paquete service?
// Contiene la logica de filtrado de propiedades
// Llama a propiedad-service via Feign para obtener
// todas las propiedades y luego las filtra segun
// los criterios que envio el cliente
// Es como un buscador inteligente de propiedades
@Service
@RequiredArgsConstructor
public class BusquedaService {

    private static final Logger log =
            LoggerFactory.getLogger(BusquedaService.class);

    // Cliente Feign para llamar a propiedad-service
    private final PropiedadClient propiedadClient;

    // ─────────────────────────────────────────────────
    // Busca propiedades aplicando filtros opcionales
    // Obtiene todas las propiedades de propiedad-service
    // y filtra segun los criterios del BusquedaRequest
    // ─────────────────────────────────────────────────
    public List<PropiedadResponse> buscar(
            BusquedaRequest request) {

        log.info("Iniciando busqueda con filtros: {}",
                request);

        try {
            // Obtenemos todas las propiedades desde
            // propiedad-service via Feign
            // GET http://propiedad-service/api/v1/propiedades
            List<PropiedadResponse> propiedades =
                    propiedadClient.listarPropiedades();

            log.info("Propiedades obtenidas: {}",
                    propiedades.size());

            // stream() permite procesar la lista
            // filter() aplica cada condicion de busqueda
            // Si el filtro viene null lo ignoramos
            // Si viene con valor filtramos por ese valor
            return propiedades.stream()

                    // Filtro por disponibilidad
                    // Si soloDisponibles es true solo muestra
                    // las que estan disponibles
                    .filter(p -> {
                        if (request.getSoloDisponibles() != null
                                && request.getSoloDisponibles()) {
                            return p.getDisponible();
                        }
                        return true;
                    })

                    // Filtro por comuna
                    // equalsIgnoreCase ignora mayusculas/minusculas
                    // "providencia" == "Providencia" == "PROVIDENCIA"
                    .filter(p -> {
                        if (request.getComuna() != null
                                && !request.getComuna().isEmpty()) {
                            return p.getComuna()
                                    .equalsIgnoreCase(
                                            request.getComuna());
                        }
                        return true;
                    })

                    // Filtro por ciudad
                    .filter(p -> {
                        if (request.getCiudad() != null
                                && !request.getCiudad().isEmpty()) {
                            return p.getCiudad()
                                    .equalsIgnoreCase(
                                            request.getCiudad());
                        }
                        return true;
                    })

                    // Filtro por precio minimo
                    // >= significa mayor o igual al precio minimo
                    .filter(p -> {
                        if (request.getPrecioMin() != null) {
                            return p.getPrecioMensual()
                                    >= request.getPrecioMin();
                        }
                        return true;
                    })

                    // Filtro por precio maximo
                    // <= significa menor o igual al precio maximo
                    .filter(p -> {
                        if (request.getPrecioMax() != null) {
                            return p.getPrecioMensual()
                                    <= request.getPrecioMax();
                        }
                        return true;
                    })

                    // Filtro por numero de habitaciones
                    // equals compara numeros exactos
                    .filter(p -> {
                        if (request.getHabitaciones() != null) {
                            return p.getHabitaciones()
                                    .equals(request.getHabitaciones());
                        }
                        return true;
                    })

                    // Filtro por metros cuadrados minimos
                    .filter(p -> {
                        if (request.getM2Min() != null) {
                            return p.getM2() >= request.getM2Min();
                        }
                        return true;
                    })

                    // Junta todos los resultados en una lista
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error al buscar propiedades: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al buscar propiedades");
        }
    }

    // ─────────────────────────────────────────────────
    // Lista solo propiedades disponibles sin filtros
    // ─────────────────────────────────────────────────
    public List<PropiedadResponse> listarDisponibles() {

        log.info("Listando propiedades disponibles");

        try {
            // Llama directamente al endpoint de disponibles
            // en propiedad-service
            return propiedadClient.listarDisponibles();
        } catch (Exception e) {
            log.error("Error al listar disponibles: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al obtener propiedades disponibles");
        }
    }
}