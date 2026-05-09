
package com.inmobiliaria.propiedadservice.repository;

import com.inmobiliaria.propiedadservice.model.Propiedad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// ¿Para que sirve este paquete repository?
// Es el acceso directo a la base de datos
// JpaRepository nos da CRUD completo gratis
// Solo definimos metodos especiales que necesitamos
// Spring genera el SQL automaticamente leyendo el nombre

public interface PropiedadRepository
        extends JpaRepository<Propiedad, Long> {

    // Busca todas las propiedades de un arrendador especifico
    // SQL generado: SELECT * FROM propiedades WHERE arrendador_id = ?
    List<Propiedad> findByArrendadorId(Long arrendadorId);

    // Busca propiedades disponibles en una comuna especifica
    // SQL generado: SELECT * FROM propiedades
    //               WHERE comuna = ? AND disponible = ?
    List<Propiedad> findByComunaAndDisponible(
            String comuna, Boolean disponible);

    // Busca todas las propiedades disponibles
    // SQL generado: SELECT * FROM propiedades WHERE disponible = ?
    List<Propiedad> findByDisponible(Boolean disponible);

    // Busca propiedades por ciudad
    // SQL generado: SELECT * FROM propiedades WHERE ciudad = ?
    List<Propiedad> findByCiudad(String ciudad);
}