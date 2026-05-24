package com.inmobiliaria.resenaservice.repository; // Paquete de acceso a datos

// ============================================================
// REPOSITORIO: RESEÑA
// ============================================================
// Acceso a la tabla "resenas" en resena_db.
// Incluye un método con @Query para calcular el promedio de calificaciones.
// ============================================================

import com.inmobiliaria.resenaservice.model.Resena;
import org.springframework.data.jpa.repository.Query;        // Para escribir JPQL
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;      // Para nombrar parámetros en @Query
import java.util.List;

public interface ResenaRepository extends CrudRepository<Resena, Long> {

    // findAll(): retorna todas las reseñas (redeclarado para retornar List en lugar de Iterable).
    List<Resena> findAll();

    // findByPropiedadId(): reseñas de una propiedad específica.
    // SQL: SELECT * FROM resenas WHERE propiedad_id = ?
    // Permite mostrar todas las evaluaciones de una propiedad.
    List<Resena> findByPropiedadId(Long propiedadId);

    // findByUsuarioId(): reseñas escritas por un usuario específico.
    // SQL: SELECT * FROM resenas WHERE usuario_id = ?
    List<Resena> findByUsuarioId(Long usuarioId);

    // @Query con JPQL para calcular el promedio de calificaciones.
    // AVG(): función de agregación que calcula el promedio de los valores.
    // "Resena r" = la clase Java (no la tabla "resenas").
    // ":propiedadId" = parámetro nombrado, conectado con @Param.
    // Retorna Double porque AVG puede retornar null si no hay reseñas.
    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.propiedadId = :propiedadId")
    Double promedioCalificacion(@Param("propiedadId") Long propiedadId);
}