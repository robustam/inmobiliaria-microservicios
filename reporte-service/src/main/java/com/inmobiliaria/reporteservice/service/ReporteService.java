
package com.inmobiliaria.reporteservice.service;

import com.inmobiliaria.reporteservice.client.PropiedadClient;
import com.inmobiliaria.reporteservice.client.ReservaClient;
import com.inmobiliaria.reporteservice.dto.request.ReporteRequest;
import com.inmobiliaria.reporteservice.dto.response.PropiedadResponse;
import com.inmobiliaria.reporteservice.dto.response.ReporteResponse;
import com.inmobiliaria.reporteservice.dto.response.ReservaResponse;
import com.inmobiliaria.reporteservice.model.Reporte;
import com.inmobiliaria.reporteservice.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// ¿Para que sirve el paquete service?
// Contiene toda la logica de negocio de los reportes
// Llama a otros microservicios via Feign para obtener datos
// Genera estadisticas y las guarda en la base de datos
@Service
@RequiredArgsConstructor
public class ReporteService {

    private static final Logger log =
            LoggerFactory.getLogger(ReporteService.class);

    // Repository para acceder a reporte_db
    private final ReporteRepository reporteRepository;

    // Cliente Feign para llamar a reserva-service
    private final ReservaClient reservaClient;

    // Cliente Feign para llamar a propiedad-service
    private final PropiedadClient propiedadClient;

    // ─────────────────────────────────────────────────
    // Genera un reporte segun el tipo solicitado
    // Llama a otros microservicios para obtener datos
    // Guarda el resultado en la base de datos
    // ─────────────────────────────────────────────────
    public ReporteResponse generarReporte(
            ReporteRequest request) {

        log.info("Generando reporte tipo: {} para periodo: {} - {}",
                request.getTipo(),
                request.getFechaInicio(),
                request.getFechaFin());

        try {
            // Verificamos que la fecha fin sea despues
            // de la fecha inicio
            if (request.getFechaFin()
                    .isBefore(request.getFechaInicio())) {
                throw new RuntimeException(
                        "La fecha fin debe ser posterior " +
                                "a la fecha inicio");
            }

            // Generamos el resultado segun el tipo de reporte
            // Cada tipo llama a un microservicio diferente
            String resultado = generarResultado(request);

            // Creamos el reporte con todos los datos
            Reporte reporte = new Reporte();
            reporte.setTipo(request.getTipo());
            reporte.setFechaInicio(request.getFechaInicio());
            reporte.setFechaFin(request.getFechaFin());
            reporte.setGeneradoPor(request.getGeneradoPor());
            reporte.setResultado(resultado);

            // Guardamos en MySQL
            Reporte guardado =
                    reporteRepository.save(reporte);

            log.info("Reporte generado con id: {}",
                    guardado.getId());

            return convertirAResponse(guardado);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al generar reporte: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al generar el reporte");
        }
    }

    // ─────────────────────────────────────────────────
    // Genera el contenido del reporte segun su tipo
    // Cada tipo obtiene datos de diferentes servicios
    // ─────────────────────────────────────────────────
    private String generarResultado(ReporteRequest request) {

        switch (request.getTipo()) {

            case PROPIEDADES:
                // Obtenemos datos de propiedad-service
                List<PropiedadResponse> propiedades =
                        propiedadClient.listarPropiedades();

                long disponibles = propiedades.stream()
                        .filter(PropiedadResponse::getDisponible)
                        .count();

                long noDisponibles = propiedades.size()
                        - disponibles;

                // Retornamos el resumen en formato JSON
                return String.format(
                        "{\"totalPropiedades\": %d, " +
                                "\"disponibles\": %d, " +
                                "\"noDisponibles\": %d}",
                        propiedades.size(),
                        disponibles,
                        noDisponibles
                );

            case INGRESOS:
                // Obtenemos propiedades para calcular ingresos
                List<PropiedadResponse> todasPropiedades =
                        propiedadClient.listarPropiedades();

                // Calculamos el ingreso potencial total
                // sumando los precios de todas las propiedades
                double ingresoTotal = todasPropiedades
                        .stream()
                        .mapToDouble(
                                PropiedadResponse::getPrecioMensual)
                        .sum();

                return String.format(
                        "{\"totalPropiedades\": %d, " +
                                "\"ingresoPotencialTotal\": %.2f}",
                        todasPropiedades.size(),
                        ingresoTotal
                );

            default:
                // Para tipos no implementados retornamos
                // un mensaje informativo
                return String.format(
                        "{\"tipo\": \"%s\", " +
                                "\"mensaje\": \"Reporte generado\", " +
                                "\"periodo\": \"%s al %s\"}",
                        request.getTipo(),
                        request.getFechaInicio(),
                        request.getFechaFin()
                );
        }
    }

    // ─────────────────────────────────────────────────
    // Obtiene un reporte por su ID
    // ─────────────────────────────────────────────────
    public ReporteResponse obtenerReporte(Long id) {

        log.info("Buscando reporte con id: {}", id);

        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Reporte no encontrado: {}", id);
                    return new RuntimeException(
                            "Reporte no encontrado");
                });

        return convertirAResponse(reporte);
    }

    // ─────────────────────────────────────────────────
    // Lista todos los reportes del sistema
    // ─────────────────────────────────────────────────
    public List<ReporteResponse> listarReportes() {

        log.info("Listando todos los reportes");

        return reporteRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────
    // Lista reportes por tipo
    // ─────────────────────────────────────────────────
    public List<ReporteResponse> listarPorTipo(
            Reporte.TipoReporte tipo) {

        log.info("Listando reportes por tipo: {}", tipo);

        return reporteRepository.findByTipo(tipo)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────
    // Convierte entidad a DTO — metodo privado
    // ─────────────────────────────────────────────────
    private ReporteResponse convertirAResponse(
            Reporte reporte) {
        return new ReporteResponse(
                reporte.getId(),
                reporte.getTipo(),
                reporte.getFechaInicio(),
                reporte.getFechaFin(),
                reporte.getGeneradoPor(),
                reporte.getResultado(),
                reporte.getFechaCreacion()
        );
    }
}