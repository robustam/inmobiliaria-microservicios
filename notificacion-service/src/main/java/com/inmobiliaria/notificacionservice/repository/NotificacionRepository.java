package com.inmobiliaria.notificacionservice.repository; // Paquete de acceso a datos

// ============================================================
// REPOSITORIO: NOTIFICACIÓN
// ============================================================
// Acceso a la tabla "notificaciones" en notificacion_db.
// Los métodos personalizados permiten filtrar por usuario, estado
// de lectura y tipo.
// ============================================================

import com.inmobiliaria.notificacionservice.model.Notificacion;
import com.inmobiliaria.notificacionservice.model.Notificacion.TipoNotificacion; // inner enum
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface NotificacionRepository extends CrudRepository<Notificacion, Long> {

    // findByUsuarioIdOrderByCreatedAtDesc():
    //   UsuarioId                = WHERE usuario_id = ?
    //   OrderByCreatedAtDesc     = ORDER BY created_at DESC (más recientes primero)
    // Retorna todas las notificaciones de un usuario ordenadas por fecha descendente.
    List<Notificacion> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

    // findByUsuarioIdAndLeidaFalseOrderByCreatedAtDesc():
    //   UsuarioId     = WHERE usuario_id = ?
    //   And           = AND
    //   LeidaFalse    = leida = false (solo las no leídas)
    //   OrderByCreatedAtDesc = ORDER BY created_at DESC
    // Retorna solo las notificaciones NO LEÍDAS de un usuario, más recientes primero.
    List<Notificacion> findByUsuarioIdAndLeidaFalseOrderByCreatedAtDesc(Long usuarioId);

    // countByUsuarioIdAndLeidaFalse():
    // SQL: SELECT COUNT(*) FROM notificaciones WHERE usuario_id = ? AND leida = false
    // Retorna el NÚMERO de notificaciones no leídas (para mostrar el badge de alertas).
    long countByUsuarioIdAndLeidaFalse(Long usuarioId);

    // findByTipo(): filtra notificaciones por tipo (RESERVA, SISTEMA, etc.)
    // SQL: SELECT * FROM notificaciones WHERE tipo = ?
    List<Notificacion> findByTipo(TipoNotificacion tipo);
}