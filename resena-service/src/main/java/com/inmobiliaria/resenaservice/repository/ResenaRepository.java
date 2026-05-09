package com.inmobiliaria.resenaservice.repository;

import com.inmobiliaria.resenaservice.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

// ¿Para que sirve este paquete repository?
// Es el acceso directo a la base de datos resena_db
// JpaRepository nos da CRUD completo gratis
// Definimos metodos especiales que necesitamos
// Spring genera el SQL automaticamente
public interface ResenaRepository
        extends JpaRepository<Resena, Long> {

    // Busca todas las resenas de una propiedad especifica
    // SQL: SELECT * FROM resenas WHERE propiedad_id = ?
    // Lo usan los clientes para ver opiniones de la propiedad
    List<Resena> findByPropiedadId(Long propiedadId);

    // Busca todas las resenas escritas por un arrendatario
    // SQL: SELECT * FROM resenas WHERE arrendatario_id = ?
    // Lo usa el arrendatario para ver sus propias resenas
    List<Resena> findByArrendatarioId(Long arrendatarioId);

    // Verifica si un arrendatario ya reseno una propiedad
    // SQL: SELECT COUNT(*) > 0 FROM resenas
    //      WHERE propiedad_id = ? AND arrendatario_id = ?
    // Evita que el mismo usuario resene dos veces la misma
    // propiedad
    Boolean existsByPropiedadIdAndArrendatarioId(
            Long propiedadId, Long arrendatarioId);

    // Calcula el promedio de puntuacion de una propiedad
    // @Query permite escribir SQL personalizado
    // AVG() es una funcion de MySQL que calcula el promedio
    // Retorna Double porque puede tener decimales: 4.5, 3.7
    @Query("SELECT AVG(r.puntuacion) FROM Resena r " +
            "WHERE r.propiedadId = :propiedadId")
    Double calcularPromedioPuntuacion(Long propiedadId);
}