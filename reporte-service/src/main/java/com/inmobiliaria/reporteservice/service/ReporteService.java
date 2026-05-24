package com.inmobiliaria.reporteservice.service; // Paquete de servicios

// ============================================================
// SERVICIO: REPORTE - LÓGICA DE NEGOCIO
// ============================================================
// Genera reportes estadísticos del sistema inmobiliario.
// Consulta datos de propiedad-service y reserva-service via Feign
// y produce resúmenes para análisis administrativo.
//
// Reportes disponibles:
//   - Propiedades: total, por estado, por tipo, por ciudad
//   - Reservas: total, por estado, ingresos de arriendos completados
//   - General: combinación de ambos reportes
//
// Cada reporte generado se guarda en reporte_db para historial.
// ============================================================

import com.inmobiliaria.reporteservice.client.PropiedadClient; // Feign → propiedad-service
import com.inmobiliaria.reporteservice.client.ReservaClient;   // Feign → reserva-service
import com.inmobiliaria.reporteservice.exception.RecursoNoEncontradoException;
import com.inmobiliaria.reporteservice.model.Reporte;
import com.inmobiliaria.reporteservice.model.Reporte.TipoReporte; // inner enum
import com.inmobiliaria.reporteservice.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors; // Para operaciones de agrupación con Stream API

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository; // acceso a tabla "reportes"
    private final PropiedadClient propiedadClient;     // Feign → propiedad-service
    private final ReservaClient reservaClient;          // Feign → reserva-service

    // Retorna todos los reportes guardados, ordenados del más reciente al más antiguo.
    public List<Reporte> findAll() {
        log.debug("Obteniendo todos los reportes");
        return reporteRepository.findAllByOrderByGeneradoEnDesc();
    }

    // Busca un reporte por ID. Lanza HTTP 404 si no existe.
    public Reporte findById(Long id) {
        log.debug("Buscando reporte con id: {}", id);
        return reporteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reporte no encontrado con id: " + id));
    }

    // Genera estadísticas de propiedades consultando propiedad-service.
    // Agrupa por estado (DISPONIBLE/ARRENDADA/INACTIVA), tipo y ciudad.
    public Map<String, Object> generarResumenPropiedades() {
        log.info("Generando reporte de resumen de propiedades");
        // Feign: GET /api/v1/propiedades/todas (incluye DISPONIBLES, ARRENDADAS e INACTIVAS)
        List<PropiedadClient.PropiedadDTO> propiedades = propiedadClient.findAll();

        // Collectors.groupingBy(): agrupa elementos por el criterio dado.
        // Collectors.counting(): cuenta cuántos elementos hay en cada grupo.
        // PropiedadDTO::getEstado: referencia al método getEstado() de PropiedadDTO.
        // Resultado: Map { "DISPONIBLE": 5, "ARRENDADA": 3, "INACTIVA": 1 }
        Map<String, Long> porEstado = propiedades.stream()
                .collect(Collectors.groupingBy(PropiedadClient.PropiedadDTO::getEstado, Collectors.counting()));

        // Agrupa por tipo: { "CASA": 4, "DEPARTAMENTO": 5 }
        Map<String, Long> porTipo = propiedades.stream()
                .collect(Collectors.groupingBy(PropiedadClient.PropiedadDTO::getTipo, Collectors.counting()));

        // Agrupa por ciudad: { "Santiago": 6, "Valparaíso": 3 }
        Map<String, Long> porCiudad = propiedades.stream()
                .collect(Collectors.groupingBy(PropiedadClient.PropiedadDTO::getCiudad, Collectors.counting()));

        // Construye el mapa de resumen con todas las estadísticas.
        Map<String, Object> resumen = Map.of(
                "totalPropiedades", propiedades.size(),
                "porEstado", porEstado,
                "porTipo", porTipo,
                "porCiudad", porCiudad
        );

        // Guarda el reporte en reporte_db para historial.
        Reporte reporte = Reporte.builder()
                .titulo("Resumen de Propiedades")
                .tipo(TipoReporte.PROPIEDADES)
                .descripcion("Estadísticas generales de propiedades")
                .datos(resumen.toString()) // serializa el mapa como texto
                .build();
        reporteRepository.save(reporte);
        log.info("Reporte de propiedades generado: total={}", propiedades.size());
        return resumen;
    }

    // Genera estadísticas de reservas consultando reserva-service.
    // Calcula el ingreso total de arriendos COMPLETADOS.
    public Map<String, Object> generarResumenReservas() {
        log.info("Generando reporte de resumen de reservas");
        // Feign: GET /api/v1/reservas (todas las reservas)
        List<ReservaClient.ReservaDTO> reservas = reservaClient.findAll();

        // Agrupa reservas por estado: { "PENDIENTE": 2, "CONFIRMADA": 3, "COMPLETADA": 5 }
        Map<String, Long> porEstado = reservas.stream()
                .collect(Collectors.groupingBy(ReservaClient.ReservaDTO::getEstado, Collectors.counting()));

        // Calcula el ingreso total solo de reservas COMPLETADAS.
        // filter(): mantiene solo las COMPLETADAS.
        // map(): extrae el monto (o ZERO si es null).
        // reduce(): suma todos los montos. BigDecimal.add() suma dos BigDecimal.
        BigDecimal ingresoTotal = reservas.stream()
                .filter(r -> "COMPLETADA".equals(r.getEstado()))
                .map(r -> r.getMonto() != null ? r.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // BigDecimal::add = suma acumulativa

        Map<String, Object> resumen = Map.of(
                "totalReservas", reservas.size(),
                "porEstado", porEstado,
                "ingresoTotal", ingresoTotal // suma de arriendos completados en CLP
        );

        // Guarda el reporte en historial.
        Reporte reporte = Reporte.builder()
                .titulo("Resumen de Reservas")
                .tipo(TipoReporte.RESERVAS)
                .descripcion("Estadísticas generales de reservas e ingresos")
                .datos(resumen.toString())
                .build();
        reporteRepository.save(reporte);
        log.info("Reporte de reservas generado: total={} ingresos={}", reservas.size(), ingresoTotal);
        return resumen;
    }

    // Genera un reporte combinado con estadísticas de propiedades Y reservas.
    // Llama a los dos métodos anteriores y combina sus resultados.
    public Map<String, Object> generarResumenGeneral() {
        log.info("Generando reporte general del sistema");
        Map<String, Object> propiedades = generarResumenPropiedades();
        Map<String, Object> reservas = generarResumenReservas();
        // Map.of() con dos mapas anidados:
        // { "propiedades": { ... }, "reservas": { ... } }
        return Map.of(
                "propiedades", propiedades,
                "reservas", reservas
        );
    }

    // Elimina un reporte del historial.
    public void delete(Long id) {
        log.info("Eliminando reporte con id: {}", id);
        findById(id); // lanza 404 si no existe
        reporteRepository.deleteById(id);
    }
}