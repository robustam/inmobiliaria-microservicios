package com.inmobiliaria.busquedaservice.service; // Paquete de servicios

// ============================================================
// SERVICIO: BÚSQUEDA - LÓGICA DE NEGOCIO
// ============================================================
// Busqueda Service es un PROXY/AGREGADOR que:
//   1. Llama a propiedad-service via Feign para obtener propiedades
//   2. Aplica filtros adicionales que propiedad-service no soporta
//      (habitaciones mínimas, metros cuadrados mínimos)
//   3. Retorna los resultados filtrados al cliente
//
// NO tiene base de datos propia para propiedades.
// Todas las propiedades vienen de propiedad-service.
// ============================================================

import com.inmobiliaria.busquedaservice.client.PropiedadClient; // Feign client
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors; // Para procesar listas con Stream API

@Slf4j
@Service
@RequiredArgsConstructor
public class BusquedaService {

    // propiedadClient: el único acceso a datos (via HTTP a propiedad-service).
    private final PropiedadClient propiedadClient;

    // Búsqueda avanzada de arriendos con múltiples filtros opcionales.
    // Los primeros 5 filtros se delegan a propiedad-service via Feign.
    // Los últimos 2 (habitacionesMin, metrosMin) se aplican aquí con Stream.
    public List<PropiedadClient.PropiedadDTO> buscar(
            String region, String ciudad, String comuna, String tipo,
            BigDecimal precioMin, BigDecimal precioMax,
            Integer habitacionesMin, Double metrosMin) {

        // log.debug() con todos los parámetros para facilitar la depuración.
        log.debug("Búsqueda de arriendos - region:{} ciudad:{} comuna:{} tipo:{} precioMin:{} precioMax:{} habitMin:{} metrosMin:{}",
                region, ciudad, comuna, tipo, precioMin, precioMax, habitacionesMin, metrosMin);

        // PASO 1: llamar a propiedad-service con los filtros básicos.
        // Feign: GET /api/v1/propiedades/buscar?region=...&ciudad=...&tipo=...
        // PASO 2: aplicar filtros adicionales con Stream API.
        List<PropiedadClient.PropiedadDTO> resultados = propiedadClient.buscar(region, ciudad, comuna, tipo, precioMin, precioMax)
                .stream() // convierte la lista en un flujo de datos para procesar
                // filter(): mantiene solo los elementos que cumplen la condición.
                // Primer filtro: si habitacionesMin es null, no filtra (todos pasan).
                //                Si tiene valor, mantiene propiedades con >= habitaciones.
                .filter(p -> habitacionesMin == null
                        || (p.getHabitaciones() != null && p.getHabitaciones() >= habitacionesMin))
                // Segundo filtro: igual pero para metros cuadrados.
                .filter(p -> metrosMin == null
                        || (p.getMetrosCuadrados() != null && p.getMetrosCuadrados() >= metrosMin))
                // collect(Collectors.toList()): termina el Stream y convierte a List.
                .collect(Collectors.toList());

        log.info("Búsqueda completada: {} resultados encontrados", resultados.size());
        return resultados;
    }

    // Retorna todas las propiedades DISPONIBLES sin filtros.
    // Ideal para mostrar el catálogo completo en la página principal.
    public List<PropiedadClient.PropiedadDTO> getDisponibles() {
        log.debug("Obteniendo arriendos disponibles");
        // Feign: GET /api/v1/propiedades (retorna solo DISPONIBLES)
        return propiedadClient.findDisponibles();
    }

    // Retorna las primeras 10 propiedades disponibles (propiedades "destacadas").
    // Usado para mostrar en la portada del sitio.
    // En una versión más avanzada, se podría ordenar por popularidad o calificación.
    public List<PropiedadClient.PropiedadDTO> getDestacadas() {
        log.debug("Obteniendo arriendos destacados");
        return propiedadClient.findDisponibles().stream()
                .limit(10) // limit(10): toma solo los primeros 10 resultados
                .collect(Collectors.toList());
    }
}