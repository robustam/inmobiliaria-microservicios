package com.inmobiliaria.propiedadservice.repository; // Paquete de acceso a datos

// ============================================================
// REPOSITORIO: PROPIEDAD
// ============================================================
// Capa de acceso a datos del servicio de propiedades.
// Extiende CrudRepository para obtener operaciones CRUD básicas:
//   save(), findById(), findAll(), deleteById(), existsById(), count()
//
// Además declaramos métodos personalizados que Spring Data JPA
// convierte automáticamente en consultas SQL.
// ============================================================

import com.inmobiliaria.propiedadservice.model.Propiedad;
import com.inmobiliaria.propiedadservice.model.Propiedad.EstadoPropiedad; // inner enum
import com.inmobiliaria.propiedadservice.model.Propiedad.TipoPropiedad;   // inner enum
import org.springframework.data.jpa.repository.Query;        // Permite escribir JPQL personalizado
import org.springframework.data.repository.CrudRepository;   // Interfaz base con operaciones CRUD
import org.springframework.data.repository.query.Param;      // Nombra parámetros en @Query
import java.math.BigDecimal; // Tipo de datos para precio
import java.util.List;       // Lista de resultados

// CrudRepository<Propiedad, Long>:
//   Propiedad = entidad que maneja este repositorio
//   Long      = tipo de la clave primaria (@Id)
public interface PropiedadRepository extends CrudRepository<Propiedad, Long> {

    // ── Métodos personalizados ──────────────────────────────

    // findAll(): retorna TODAS las propiedades de la tabla.
    // CrudRepository incluye findAll() pero retorna Iterable<T>, no List<T>.
    // Al redeclararlo aquí con List<Propiedad>, Spring genera la versión que retorna List.
    // SQL generado: SELECT * FROM propiedades
    List<Propiedad> findAll();

    // findByEstado(): propiedades filtradas por estado.
    // SQL: SELECT * FROM propiedades WHERE estado = ?
    // Ejemplo: findByEstado(DISPONIBLE) → solo propiedades disponibles
    List<Propiedad> findByEstado(EstadoPropiedad estado);

    // findByPropietarioId(): propiedades de un propietario específico.
    // SQL: SELECT * FROM propiedades WHERE propietario_id = ?
    List<Propiedad> findByPropietarioId(Long propietarioId);

    // findByRegionIgnoreCaseAndEstado(): propiedades en una región con un estado.
    // "IgnoreCase" = la comparación no distingue mayúsculas/minúsculas.
    //   "Metropolitana" = "metropolitana" = "METROPOLITANA"
    // "And" une dos condiciones (ambas deben cumplirse).
    // SQL: SELECT * FROM propiedades WHERE LOWER(region) = LOWER(?) AND estado = ?
    List<Propiedad> findByRegionIgnoreCaseAndEstado(String region, EstadoPropiedad estado);

    // Igual que el anterior pero filtrado por comuna en lugar de región.
    List<Propiedad> findByComunaIgnoreCaseAndEstado(String comuna, EstadoPropiedad estado);

    // ── Consulta personalizada con JPQL ─────────────────────

    // @Query: permite escribir consultas JPQL (Java Persistence Query Language).
    // JPQL es como SQL pero usa nombres de clases y campos Java, no tablas y columnas.
    // "Propiedad p" = la clase Java (no la tabla "propiedades")
    // "p.region" = el campo Java (no la columna "region")
    //
    // La consulta busca propiedades DISPONIBLES con múltiples filtros opcionales.
    // ":region IS NULL OR ..." = si el parámetro es null, se ignora ese filtro.
    // LOWER(CONCAT('%', :region, '%')) = búsqueda parcial sin importar mayúsculas.
    //   Ejemplo: "Santiago" encontraría "Gran Santiago" y "Santiago Centro"
    @Query("SELECT p FROM Propiedad p WHERE p.estado = 'DISPONIBLE' " +
           "AND (:region IS NULL OR LOWER(p.region) LIKE LOWER(CONCAT('%', :region, '%'))) " +
           "AND (:ciudad IS NULL OR LOWER(p.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%'))) " +
           "AND (:comuna IS NULL OR LOWER(p.comuna) LIKE LOWER(CONCAT('%', :comuna, '%'))) " +
           "AND (:tipo IS NULL OR p.tipo = :tipo) " +
           "AND (:precioMin IS NULL OR p.precio >= :precioMin) " +
           "AND (:precioMax IS NULL OR p.precio <= :precioMax)")
    // @Param("nombre"): conecta el parámetro Java con el :nombre en la consulta JPQL.
    List<Propiedad> buscar(
            @Param("region")    String region,        // filtro de región (opcional)
            @Param("ciudad")    String ciudad,         // filtro de ciudad (opcional)
            @Param("comuna")    String comuna,         // filtro de comuna (opcional)
            @Param("tipo")      TipoPropiedad tipo,    // filtro de tipo (opcional)
            @Param("precioMin") BigDecimal precioMin,  // precio mínimo (opcional)
            @Param("precioMax") BigDecimal precioMax); // precio máximo (opcional)
}