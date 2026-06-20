package com.inmobiliaria.reservaservice.service; // Paquete de servicios

// ============================================================
// SERVICIO: RESERVA - LÓGICA DE NEGOCIO
// ============================================================
// Gestiona el ciclo de vida de los arriendos en el sistema.
//
// INTEGRACIÓN con propiedad-service via Feign:
//   Al crear una reserva → verifica que la propiedad esté DISPONIBLE
//                        → cambia el estado a ARRENDADA
//   Al cancelar/completar → cambia el estado de vuelta a DISPONIBLE
//
// Reglas de negocio implementadas:
//   - fechaFin no puede ser antes que fechaInicio
//   - La propiedad debe estar en estado DISPONIBLE para reservar
//   - Si no se especifica monto, se toma el precio de la propiedad
// ============================================================

import com.inmobiliaria.reservaservice.client.PropiedadClient;               // Feign client
import com.inmobiliaria.reservaservice.exception.GlobalExceptionHandler.NegocioException;            // Error 400
import com.inmobiliaria.reservaservice.exception.GlobalExceptionHandler.RecursoNoEncontradoException; // Error 404
import com.inmobiliaria.reservaservice.model.Reserva;                         // Entidad
import com.inmobiliaria.reservaservice.model.Reserva.EstadoReserva;           // inner enum
import com.inmobiliaria.reservaservice.repository.ReservaRepository;          // Acceso BD
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository; // acceso a tabla "reservas"
    private final PropiedadClient propiedadClient;     // cliente HTTP para propiedad-service

    // Retorna todas las reservas del sistema.
    public List<Reserva> findAll() {
        log.debug("Obteniendo todas las reservas");
        return reservaRepository.findAll();
    }

    // Busca una reserva por ID. Lanza HTTP 404 si no existe.
    public Reserva findById(Long id) {
        log.debug("Buscando reserva con id: {}", id);
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada con id: " + id));
    }

    // Retorna todas las reservas de un usuario específico.
    public List<Reserva> findByUsuario(Long usuarioId) {
        log.debug("Buscando reservas del usuario: {}", usuarioId);
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    // Retorna todas las reservas de una propiedad específica (historial).
    public List<Reserva> findByPropiedad(Long propiedadId) {
        log.debug("Buscando reservas de la propiedad: {}", propiedadId);
        return reservaRepository.findByPropiedadId(propiedadId);
    }

    // Crea una nueva reserva con validaciones de negocio.
    // INTEGRACIÓN: llama a propiedad-service via Feign.
    public Reserva create(Reserva reserva) {
        log.info("Creando reserva - propiedad:{} usuario:{}", reserva.getPropiedadId(), reserva.getUsuarioId());

        // VALIDACIÓN 1: fechas coherentes.
        // isBefore(): retorna true si fechaFin es anterior a fechaInicio.
        if (reserva.getFechaFin().isBefore(reserva.getFechaInicio())) {
            throw new NegocioException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        // INTEGRACIÓN: consulta propiedad-service para obtener datos de la propiedad.
        // Feign traduce esto a: GET http://propiedad-service/api/v1/propiedades/{id}
        PropiedadClient.PropiedadDTO propiedad = propiedadClient.findById(reserva.getPropiedadId());

        // VALIDACIÓN 2: la propiedad debe estar DISPONIBLE.
        if (!"DISPONIBLE".equals(propiedad.getEstado())) {
            throw new NegocioException("La propiedad no está disponible para arrendar");
        }

        // Si no se especificó monto, se usa el precio de la propiedad.
        if (reserva.getMonto() == null) {
            reserva.setMonto(propiedad.getPrecio());
        }

        // INTEGRACIÓN: cambia el estado de la propiedad a ARRENDADA.
        // Feign: PATCH http://propiedad-service/api/v1/propiedades/{id}/estado?estado=ARRENDADA
        propiedadClient.cambiarEstado(reserva.getPropiedadId(), "ARRENDADA");

        // Guarda la reserva en la BD local (reserva_db).
        Reserva guardada = reservaRepository.save(reserva);
        log.info("Reserva creada con id: {}", guardada.getId());
        return guardada;
    }

    // Cambia el estado de una reserva.
    // Si se cancela o completa, libera la propiedad (vuelve a DISPONIBLE).
    public Reserva cambiarEstado(Long id, String estado) {
        log.info("Cambiando estado de reserva {} a {}", id, estado);
        Reserva reserva = findById(id); // lanza 404 si no existe

        // valueOf() convierte el String al enum. Si el valor no existe → IllegalArgumentException.
        EstadoReserva nuevoEstado = EstadoReserva.valueOf(estado.toUpperCase());
        reserva.setEstado(nuevoEstado);

        // Si la reserva se cancela o completa, la propiedad queda libre.
        if (nuevoEstado == EstadoReserva.CANCELADA || nuevoEstado == EstadoReserva.COMPLETADA) {
            log.info("Liberando propiedad {} por reserva {} -> {}", reserva.getPropiedadId(), id, estado);
            // INTEGRACIÓN: cambia la propiedad de vuelta a DISPONIBLE en propiedad-service.
            propiedadClient.cambiarEstado(reserva.getPropiedadId(), "DISPONIBLE");
        }

        return reservaRepository.save(reserva); // guarda el nuevo estado
    }

    // Elimina físicamente una reserva de la BD y libera la propiedad.
    public void delete(Long id) {
        log.info("Eliminando reserva con id: {}", id);
        Reserva reserva = findById(id); // lanza 404 si no existe
        // Libera la propiedad antes de eliminar la reserva.
        propiedadClient.cambiarEstado(reserva.getPropiedadId(), "DISPONIBLE");
        // deleteById(): DELETE FROM reservas WHERE id = ?
        reservaRepository.deleteById(id);
    }
}