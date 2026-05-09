
package com.inmobiliaria.reservaservice.service;

import com.inmobiliaria.reservaservice.client.PropiedadClient;
import com.inmobiliaria.reservaservice.dto.request.ReservaRequest;
import com.inmobiliaria.reservaservice.dto.response.PropiedadResponse;
import com.inmobiliaria.reservaservice.dto.response.ReservaResponse;
import com.inmobiliaria.reservaservice.model.Reserva;
import com.inmobiliaria.reservaservice.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// ¿Para que sirve el paquete service?
// Contiene toda la logica de negocio de las reservas
// Es la capa mas importante — aqui se toman las decisiones
// Ejemplo: verificar disponibilidad antes de crear reserva
@Service
@RequiredArgsConstructor
public class ReservaService {

    private static final Logger log =
            LoggerFactory.getLogger(ReservaService.class);

    // Repository para acceder a reserva_db
    private final ReservaRepository reservaRepository;

    // Cliente Feign para llamar a propiedad-service
    // Feign hace las llamadas HTTP automaticamente
    private final PropiedadClient propiedadClient;

    // ─────────────────────────────────────────────────
    // Crea una nueva reserva
    // Verifica disponibilidad antes de crear
    // ─────────────────────────────────────────────────
    public ReservaResponse crearReserva(ReservaRequest request) {

        log.info("Iniciando creacion de reserva para propiedad: {}",
                request.getPropiedadId());

        try {
            // Verificamos que la fecha fin sea despues
            // de la fecha inicio
            if (!request.getFechaFin()
                    .isAfter(request.getFechaInicio())) {
                log.warn("Fechas incorrectas — fin antes que inicio");
                throw new RuntimeException(
                        "La fecha de fin debe ser posterior " +
                                "a la fecha de inicio");
            }

            // Llamamos a propiedad-service via Feign
            // para verificar que la propiedad existe
            // y esta disponible
            log.info("Consultando disponibilidad de propiedad: {}",
                    request.getPropiedadId());

            PropiedadResponse propiedad =
                    propiedadClient.getPropiedadById(
                            request.getPropiedadId());

            // Si la propiedad no esta disponible
            // no podemos crear la reserva
            if (!propiedad.getDisponible()) {
                log.warn("Propiedad no disponible: {}",
                        request.getPropiedadId());
                throw new RuntimeException(
                        "La propiedad no esta disponible " +
                                "para reservar");
            }

            // Creamos la reserva con estado PENDIENTE
            // El arrendador debe aprobarla despues
            Reserva reserva = new Reserva();
            reserva.setPropiedadId(request.getPropiedadId());
            reserva.setArrendatarioId(
                    request.getArrendatarioId());
            reserva.setFechaInicio(request.getFechaInicio());
            reserva.setFechaFin(request.getFechaFin());
            reserva.setMensajeSolicitud(
                    request.getMensajeSolicitud());

            // Estado inicial siempre es PENDIENTE
            reserva.setEstado(Reserva.Estado.PENDIENTE);

            // Guardamos la reserva en MySQL
            Reserva guardada = reservaRepository.save(reserva);

            log.info("Reserva creada con id: {}",
                    guardada.getId());

            return convertirAResponse(guardada);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al crear reserva: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al crear la reserva");
        }
    }

    // ─────────────────────────────────────────────────
    // Aprueba una reserva pendiente
    // Cambia estado a APROBADA y propiedad a no disponible
    // ─────────────────────────────────────────────────
    public ReservaResponse aprobarReserva(Long id) {

        log.info("Aprobando reserva: {}", id);

        // Buscamos la reserva — si no existe lanza error
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Reserva no encontrada"));

        // Solo se pueden aprobar reservas PENDIENTES
        if (!reserva.getEstado()
                .equals(Reserva.Estado.PENDIENTE)) {
            throw new RuntimeException(
                    "Solo se pueden aprobar reservas pendientes");
        }

        // Cambiamos el estado a APROBADA
        reserva.setEstado(Reserva.Estado.APROBADA);
        Reserva actualizada = reservaRepository.save(reserva);

        // Llamamos a propiedad-service via Feign
        // para marcar la propiedad como no disponible
        // Asi nadie mas puede reservarla
        log.info("Marcando propiedad {} como no disponible",
                reserva.getPropiedadId());
        propiedadClient.cambiarDisponibilidad(
                reserva.getPropiedadId(), false);

        log.info("Reserva aprobada: {}", id);

        return convertirAResponse(actualizada);
    }

    // ─────────────────────────────────────────────────
    // Rechaza una reserva pendiente
    // ─────────────────────────────────────────────────
    public ReservaResponse rechazarReserva(Long id) {

        log.info("Rechazando reserva: {}", id);

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Reserva no encontrada"));

        // Solo se pueden rechazar reservas PENDIENTES
        if (!reserva.getEstado()
                .equals(Reserva.Estado.PENDIENTE)) {
            throw new RuntimeException(
                    "Solo se pueden rechazar reservas pendientes");
        }

        // Cambiamos el estado a RECHAZADA
        reserva.setEstado(Reserva.Estado.RECHAZADA);
        Reserva actualizada = reservaRepository.save(reserva);

        log.info("Reserva rechazada: {}", id);

        return convertirAResponse(actualizada);
    }

    // ─────────────────────────────────────────────────
    // Cancela una reserva aprobada o pendiente
    // Si estaba aprobada libera la propiedad
    // ─────────────────────────────────────────────────
    public ReservaResponse cancelarReserva(Long id) {

        log.info("Cancelando reserva: {}", id);

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Reserva no encontrada"));

        // Solo se pueden cancelar reservas PENDIENTES o APROBADAS
        if (reserva.getEstado()
                .equals(Reserva.Estado.RECHAZADA) ||
                reserva.getEstado()
                        .equals(Reserva.Estado.CANCELADA)) {
            throw new RuntimeException(
                    "No se puede cancelar esta reserva");
        }

        // Si la reserva estaba APROBADA
        // la propiedad vuelve a estar disponible
        if (reserva.getEstado()
                .equals(Reserva.Estado.APROBADA)) {
            log.info("Liberando propiedad: {}",
                    reserva.getPropiedadId());
            propiedadClient.cambiarDisponibilidad(
                    reserva.getPropiedadId(), true);
        }

        // Cambiamos el estado a CANCELADA
        reserva.setEstado(Reserva.Estado.CANCELADA);
        Reserva actualizada = reservaRepository.save(reserva);

        log.info("Reserva cancelada: {}", id);

        return convertirAResponse(actualizada);
    }

    // ─────────────────────────────────────────────────
    // Lista reservas de un arrendatario
    // ─────────────────────────────────────────────────
    public List<ReservaResponse> listarPorArrendatario(
            Long arrendatarioId) {

        log.info("Listando reservas del arrendatario: {}",
                arrendatarioId);

        return reservaRepository
                .findByArrendatarioId(arrendatarioId)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────
    // Lista reservas de una propiedad
    // ─────────────────────────────────────────────────
    public List<ReservaResponse> listarPorPropiedad(
            Long propiedadId) {

        log.info("Listando reservas de propiedad: {}",
                propiedadId);

        return reservaRepository
                .findByPropiedadId(propiedadId)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────
    // Obtiene una reserva por su ID
    // ─────────────────────────────────────────────────
    public ReservaResponse obtenerReserva(Long id) {

        log.info("Buscando reserva con id: {}", id);

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Reserva no encontrada"));

        return convertirAResponse(reserva);
    }

    // ─────────────────────────────────────────────────
    // Convierte entidad a DTO — metodo privado
    // ─────────────────────────────────────────────────
    private ReservaResponse convertirAResponse(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getPropiedadId(),
                reserva.getArrendatarioId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getEstado(),
                reserva.getMensajeSolicitud(),
                reserva.getFechaCreacion()
        );
    }
}
