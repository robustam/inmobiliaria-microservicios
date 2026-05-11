
package com.inmobiliaria.notificacionservice.service;

import com.inmobiliaria.notificacionservice.dto.request.NotificacionRequest;
import com.inmobiliaria.notificacionservice.dto.response.NotificacionResponse;
import com.inmobiliaria.notificacionservice.model.Notificacion;
import com.inmobiliaria.notificacionservice.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// ¿Para que sirve el paquete service?
// Contiene toda la logica de negocio de las notificaciones
// Crea notificaciones, las marca como leidas
// y las lista por usuario
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private static final Logger log =
            LoggerFactory.getLogger(NotificacionService.class);

    // Repository para acceder a notificacion_db
    private final NotificacionRepository notificacionRepository;

    // ─────────────────────────────────────────────────
    // Crea una nueva notificacion para un usuario
    // Lo llaman otros microservicios internamente
    // Ejemplo: reserva-service llama aqui cuando
    // aprueba una reserva
    // ─────────────────────────────────────────────────
    public NotificacionResponse crearNotificacion(
            NotificacionRequest request) {

        log.info("Creando notificacion para usuario: {}",
                request.getUsuarioId());

        try {
            // Creamos la notificacion con los datos del request
            Notificacion notificacion = new Notificacion();
            notificacion.setUsuarioId(request.getUsuarioId());
            notificacion.setTipo(request.getTipo());
            notificacion.setMensaje(request.getMensaje());

            // Por defecto nace sin leer
            notificacion.setLeido(false);

            // Guardamos en MySQL
            Notificacion guardada =
                    notificacionRepository.save(notificacion);

            log.info("Notificacion creada con id: {}",
                    guardada.getId());

            return convertirAResponse(guardada);

        } catch (Exception e) {
            log.error("Error al crear notificacion: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al crear la notificacion");
        }
    }

    // ─────────────────────────────────────────────────
    // Lista todas las notificaciones de un usuario
    // Ordenadas de mas reciente a mas antigua
    // ─────────────────────────────────────────────────
    public List<NotificacionResponse> listarPorUsuario(
            Long usuarioId) {

        log.info("Listando notificaciones del usuario: {}",
                usuarioId);

        return notificacionRepository
                .findByUsuarioIdOrderByFechaCreacionDesc(
                        usuarioId)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────
    // Lista solo notificaciones sin leer de un usuario
    // ─────────────────────────────────────────────────
    public List<NotificacionResponse> listarSinLeer(
            Long usuarioId) {

        log.info("Listando notificaciones sin leer → usuario: {}",
                usuarioId);

        // false = sin leer
        return notificacionRepository
                .findByUsuarioIdAndLeido(usuarioId, false)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────
    // Cuenta notificaciones sin leer de un usuario
    // Util para mostrar el numero de avisos pendientes
    // Ejemplo: el icono de campana con numero rojo
    // ─────────────────────────────────────────────────
    public Long contarSinLeer(Long usuarioId) {

        log.info("Contando notificaciones sin leer → usuario: {}",
                usuarioId);

        // false = sin leer
        return notificacionRepository
                .countByUsuarioIdAndLeido(usuarioId, false);
    }

    // ─────────────────────────────────────────────────
    // Marca una notificacion como leida
    // El usuario la vio y ya no aparece como nueva
    // ─────────────────────────────────────────────────
    public NotificacionResponse marcarComoLeida(Long id) {

        log.info("Marcando notificacion como leida: {}", id);

        // Buscamos la notificacion — si no existe lanza error
        Notificacion notificacion =
                notificacionRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException(
                                "Notificacion no encontrada"));

        // Cambiamos leido a true
        notificacion.setLeido(true);
        Notificacion actualizada =
                notificacionRepository.save(notificacion);

        log.info("Notificacion marcada como leida: {}", id);

        return convertirAResponse(actualizada);
    }

    // ─────────────────────────────────────────────────
    // Marca TODAS las notificaciones de un usuario
    // como leidas de una sola vez
    // ─────────────────────────────────────────────────
    public void marcarTodasComoLeidas(Long usuarioId) {

        log.info("Marcando todas como leidas → usuario: {}",
                usuarioId);

        // Obtenemos todas las sin leer
        List<Notificacion> sinLeer =
                notificacionRepository
                        .findByUsuarioIdAndLeido(
                                usuarioId, false);

        // Marcamos cada una como leida
        // forEach recorre cada notificacion
        sinLeer.forEach(n -> n.setLeido(true));

        // saveAll guarda todos los cambios de una vez
        // Es mas eficiente que guardar uno por uno
        notificacionRepository.saveAll(sinLeer);

        log.info("Total marcadas como leidas: {}",
                sinLeer.size());
    }

    // ─────────────────────────────────────────────────
    // Convierte entidad a DTO — metodo privado
    // ─────────────────────────────────────────────────
    private NotificacionResponse convertirAResponse(
            Notificacion notificacion) {
        return new NotificacionResponse(
                notificacion.getId(),
                notificacion.getUsuarioId(),
                notificacion.getTipo(),
                notificacion.getMensaje(),
                notificacion.getLeido(),
                notificacion.getFechaCreacion()
        );
    }
}