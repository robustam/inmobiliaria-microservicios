
package com.inmobiliaria.reservaservice.repository;

import com.inmobiliaria.reservaservice.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// ¿Para que sirve este paquete repository?
// Es el acceso directo a la base de datos reserva_db
// JpaRepository nos da CRUD completo gratis
// Solo definimos metodos especiales que necesitamos
// Spring genera el SQL automaticamente

public interface ReservaRepository
        extends JpaRepository<Reserva, Long> {

    // Busca todas las reservas de un arrendatario
    // SQL: SELECT * FROM reservas WHERE arrendatario_id = ?
    // Lo usa el arrendatario para ver sus reservas
    List<Reserva> findByArrendatarioId(Long arrendatarioId);

    // Busca todas las reservas de una propiedad especifica
    // SQL: SELECT * FROM reservas WHERE propiedad_id = ?
    // Lo usa el arrendador para ver reservas de su propiedad
    List<Reserva> findByPropiedadId(Long propiedadId);

    // Busca reservas de una propiedad con un estado especifico
    // SQL: SELECT * FROM reservas
    //      WHERE propiedad_id = ? AND estado = ?
    // Ejemplo: buscar reservas APROBADAS de la propiedad 1
    List<Reserva> findByPropiedadIdAndEstado(
            Long propiedadId, Reserva.Estado estado);

    // Busca reservas de un arrendatario con un estado especifico
    // SQL: SELECT * FROM reservas
    //      WHERE arrendatario_id = ? AND estado = ?
    // Ejemplo: ver todas las reservas PENDIENTES del usuario
    List<Reserva> findByArrendatarioIdAndEstado(
            Long arrendatarioId, Reserva.Estado estado);
}