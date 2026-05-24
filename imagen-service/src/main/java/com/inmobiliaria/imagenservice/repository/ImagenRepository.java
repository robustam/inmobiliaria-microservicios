package com.inmobiliaria.imagenservice.repository; // Paquete de acceso a datos

// ============================================================
// REPOSITORIO: IMAGEN
// ============================================================
// Acceso a la tabla "imagenes" en imagen_db.
// Permite buscar imágenes de propiedades, encontrar la foto
// principal y contar cuántas imágenes tiene una propiedad.
// ============================================================

import com.inmobiliaria.imagenservice.model.Imagen;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface ImagenRepository extends CrudRepository<Imagen, Long> {

    // findAll(): lista todas las imágenes (redeclarado para retornar List).
    List<Imagen> findAll();

    // findByPropiedadIdOrderByPrincipalDescCreatedAtAsc():
    //   PropiedadId                = WHERE propiedad_id = ?
    //   OrderByPrincipalDesc       = ORDER BY principal DESC (true primero, luego false)
    //   CreatedAtAsc               = luego ordenado por fecha ascendente (más antiguas primero)
    //
    // SQL: SELECT * FROM imagenes WHERE propiedad_id = ?
    //      ORDER BY principal DESC, created_at ASC
    //
    // Resultado: la imagen principal aparece primero, luego el resto en orden de subida.
    List<Imagen> findByPropiedadIdOrderByPrincipalDescCreatedAtAsc(Long propiedadId);

    // findByPropiedadIdAndPrincipalTrue():
    //   PropiedadId    = WHERE propiedad_id = ?
    //   PrincipalTrue  = AND principal = true
    //
    // SQL: SELECT * FROM imagenes WHERE propiedad_id = ? AND principal = true LIMIT 1
    // Retorna Optional porque puede no haber imagen principal configurada.
    Optional<Imagen> findByPropiedadIdAndPrincipalTrue(Long propiedadId);

    // countByPropiedadId(): cuenta cuántas imágenes tiene una propiedad.
    // SQL: SELECT COUNT(*) FROM imagenes WHERE propiedad_id = ?
    long countByPropiedadId(Long propiedadId);
}