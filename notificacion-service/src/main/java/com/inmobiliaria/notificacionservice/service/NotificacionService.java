package com.inmobiliaria.notificacionservice.service; // Paquete de servicios

// ============================================================
// SERVICIO: NOTIFICACIÓN - LÓGICA DE NEGOCIO
// ============================================================
// Gestiona el sistema de notificaciones del sistema inmobiliario.
//
// Responsabilidades:
//   - Crear y enviar notificaciones a usuarios
//   - Listar notificaciones (todas o solo no leídas)
//   - Marcar notificaciones como leídas (una o todas)
//   - Obtener resumen de notificaciones pendientes
//   - Eliminar notificaciones
// ============================================================

import com.inmobiliaria.notificacionservice.exception.RecursoNoEncontradoException; // Error 404
import com.inmobiliaria.notificacionservice.model.Notificacion;                     // Entidad
import com.inmobiliaria.notificacionservice.repository.NotificacionRepository;      // Acceso BD
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    // Retorna TODAS las notificaciones de un usuario, ordenadas de la más reciente a la más antigua.
    public List<Notificacion> findByUsuario(Long usuarioId) {
        log.debug("Obteniendo notificaciones del usuario: {}", usuarioId);
        return notificacionRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId);
    }

    // Retorna solo las notificaciones NO LEÍDAS de un usuario (leida = false).
    // Ordenadas de la más reciente a la más antigua.
    public List<Notificacion> findNoLeidasByUsuario(Long usuarioId) {
        log.debug("Obteniendo notificaciones no leídas del usuario: {}", usuarioId);
        return notificacionRepository.findByUsuarioIdAndLeidaFalseOrderByCreatedAtDesc(usuarioId);
    }

    // Calcula el resumen de notificaciones de un usuario.
    // Retorna: { usuarioId, total (todas), noLeidas (pendientes) }
    public Map<String, Object> getResumenUsuario(Long usuarioId) {
        // countByUsuarioIdAndLeidaFalse(): COUNT SQL eficiente, no carga todos los datos.
        long noLeidas = notificacionRepository.countByUsuarioIdAndLeidaFalse(usuarioId);
        long total = notificacionRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId).size();
        log.debug("Resumen usuario {}: total={} noLeidas={}", usuarioId, total, noLeidas);
        return Map.of("usuarioId", usuarioId, "total", total, "noLeidas", noLeidas);
    }

    // Busca una notificación por ID. Lanza HTTP 404 si no existe.
    public Notificacion findById(Long id) {
        log.debug("Buscando notificación con id: {}", id);
        return notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada con id: " + id));
    }

    // Crea y guarda una nueva notificación.
    // @PrePersist en la entidad asigna createdAt y tipo por defecto.
    public Notificacion create(Notificacion notificacion) {
        log.info("Creando notificación para usuario: {} - tipo: {}", notificacion.getUsuarioId(), notificacion.getTipo());
        return notificacionRepository.save(notificacion);
    }

    // Marca UNA notificación como leída y registra la fecha/hora de lectura.
    public Notificacion marcarLeida(Long id) {
        log.info("Marcando notificación {} como leída", id);
        Notificacion notificacion = findById(id); // lanza 404 si no existe
        notificacion.setLeida(true);              // actualiza la bandera
        notificacion.setLeidaAt(LocalDateTime.now()); // registra cuándo se leyó
        return notificacionRepository.save(notificacion); // guarda cambios
    }

    // Marca TODAS las notificaciones no leídas de un usuario como leídas.
    // Retorna el número de notificaciones que se marcaron.
    public int marcarTodasLeidas(Long usuarioId) {
        log.info("Marcando todas las notificaciones del usuario {} como leídas", usuarioId);
        List<Notificacion> noLeidas = findNoLeidasByUsuario(usuarioId);
        LocalDateTime ahora = LocalDateTime.now();
        // forEach(): aplica la función a cada elemento de la lista.
        noLeidas.forEach(n -> {
            n.setLeida(true);    // marca como leída
            n.setLeidaAt(ahora); // registra la fecha de lectura (la misma para todas)
        });
        // saveAll(): guarda TODOS los cambios en un solo batch de SQL (eficiente).
        notificacionRepository.saveAll(noLeidas);
        log.debug("Se marcaron {} notificaciones como leídas para usuario: {}", noLeidas.size(), usuarioId);
        return noLeidas.size(); // retorna cuántas se marcaron
    }

    // Elimina físicamente una notificación de la BD.
    public void delete(Long id) {
        log.info("Eliminando notificación con id: {}", id);
        findById(id); // lanza 404 si no existe
        notificacionRepository.deleteById(id);
    }
}