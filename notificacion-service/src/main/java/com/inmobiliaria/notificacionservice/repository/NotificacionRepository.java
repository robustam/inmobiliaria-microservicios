
package com.inmobiliaria.notificacionservice.repository;

import com.inmobiliaria.notificacionservice.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// ¿Para que sirve este paquete repository?
// Es el acceso directo a la base de datos notificacion_db
// JpaRepository nos da CRUD completo gratis
// Definimos metodos especiales que necesitamos
// Spring genera el SQL automaticamente
public interface NotificacionRepository
        extends JpaRepository<Notificacion, Long> {

    // Busca todas las notificaciones de un usuario
    // SQL: SELECT * FROM notificaciones
    //      WHERE usuario_id = ?
    //      ORDER BY fecha_creacion DESC
    // El usuario ve todas sus notificaciones
    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(
            Long usuarioId);

    // Busca notificaciones sin leer de un usuario
    // SQL: SELECT * FROM notificaciones
    //      WHERE usuario_id = ? AND leido = false
    // Muestra solo las notificaciones nuevas
    List<Notificacion> findByUsuarioIdAndLeido(
            Long usuarioId, Boolean leido);

    // Cuenta cuantas notificaciones sin leer tiene un usuario
    // SQL: SELECT COUNT(*) FROM notificaciones
    //      WHERE usuario_id = ? AND leido = false
    // Util para mostrar el numero de avisos pendientes
    Long countByUsuarioIdAndLeido(
            Long usuarioId, Boolean leido);
}