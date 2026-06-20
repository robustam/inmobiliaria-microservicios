package com.inmobiliaria.reservaservice.repository; // Paquete de acceso a datos

// ============================================================
// REPOSITORIO: RESERVA
// ============================================================
// Acceso a la tabla "reservas" en reserva_db.
// CrudRepository provee operaciones CRUD básicas automáticamente.
// ============================================================

import com.inmobiliaria.reservaservice.model.Reserva;
import com.inmobiliaria.reservaservice.model.Reserva.EstadoReserva; // inner enum de Reserva
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ReservaRepository extends CrudRepository<Reserva, Long> {

    // findAll(): retorna todas las reservas (CrudRepository retorna Iterable, aquí List).
    List<Reserva> findAll();

    // findByUsuarioId(): reservas de un usuario específico.
    // SQL: SELECT * FROM reservas WHERE usuario_id = ?
    List<Reserva> findByUsuarioId(Long usuarioId);

    // findByPropiedadId(): reservas de una propiedad específica.
    // Útil para ver el historial de arrendatarios de una propiedad.
    // SQL: SELECT * FROM reservas WHERE propiedad_id = ?
    List<Reserva> findByPropiedadId(Long propiedadId);

    // findByEstado(): reservas filtradas por estado (PENDIENTE, CONFIRMADA, etc.).
    // SQL: SELECT * FROM reservas WHERE estado = ?
    List<Reserva> findByEstado(EstadoReserva estado);

    // findByUsuarioIdAndEstado(): reservas de un usuario con un estado específico.
    // "And" une dos condiciones: WHERE usuario_id = ? AND estado = ?
    // Ejemplo: reservas CONFIRMADAS del usuario 5
    List<Reserva> findByUsuarioIdAndEstado(Long usuarioId, EstadoReserva estado);

    // existsByPropiedadIdAndEstadoIn(): verifica si una propiedad tiene reservas activas.
    // "In" = estado debe estar en la lista de estados.
    // SQL: SELECT COUNT(*) > 0 FROM reservas WHERE propiedad_id = ? AND estado IN (?, ?, ...)
    // Usado para saber si una propiedad tiene reservas PENDIENTES o CONFIRMADAS.
    boolean existsByPropiedadIdAndEstadoIn(Long propiedadId, List<EstadoReserva> estados);

    // findByEstadoInAndFechaFinBefore(): reservas cuya fecha de fin ya pasó y siguen activas.
    // Usado por el scheduler para detectar arriendos vencidos automáticamente.
    // SQL: SELECT * FROM reservas WHERE estado IN (?, ...) AND fecha_fin < ?
    List<Reserva> findByEstadoInAndFechaFinBefore(List<EstadoReserva> estados, java.time.LocalDate fecha);
}