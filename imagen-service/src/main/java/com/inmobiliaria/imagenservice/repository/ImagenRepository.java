
package com.inmobiliaria.imagenservice.repository;

import com.inmobiliaria.imagenservice.model.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// ¿Para que sirve este paquete repository?
// Es el acceso directo a la base de datos imagen_db
// JpaRepository nos da CRUD completo gratis
// Definimos metodos especiales que necesitamos
// Spring genera el SQL automaticamente
public interface ImagenRepository
        extends JpaRepository<Imagen, Long> {

    // Busca todas las imagenes de una entidad especifica
    // SQL: SELECT * FROM imagenes
    //      WHERE entidad_id = ? AND tipo_entidad = ?
    // Ejemplo: obtener todas las fotos de la propiedad 5
    List<Imagen> findByEntidadIdAndTipoEntidad(
            Long entidadId, Imagen.TipoEntidad tipoEntidad);

    // Busca todas las imagenes por tipo de entidad
    // SQL: SELECT * FROM imagenes WHERE tipo_entidad = ?
    // Ejemplo: obtener todas las fotos de perfil
    List<Imagen> findByTipoEntidad(
            Imagen.TipoEntidad tipoEntidad);

    // Cuenta cuantas imagenes tiene una entidad
    // SQL: SELECT COUNT(*) FROM imagenes
    //      WHERE entidad_id = ? AND tipo_entidad = ?
    // Util para limitar el numero de fotos por propiedad
    Long countByEntidadIdAndTipoEntidad(
            Long entidadId, Imagen.TipoEntidad tipoEntidad);
}