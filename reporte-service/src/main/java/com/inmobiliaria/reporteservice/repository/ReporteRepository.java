
package com.inmobiliaria.reporteservice.repository;

import com.inmobiliaria.reporteservice.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// ¿Para que sirve este paquete repository?
// Es el acceso directo a la base de datos reporte_db
// JpaRepository nos da CRUD completo gratis
// Definimos metodos especiales que necesitamos
// Spring genera el SQL automaticamente
public interface ReporteRepository
        extends JpaRepository<Reporte, Long> {

    // Busca todos los reportes de un tipo especifico
    // SQL: SELECT * FROM reportes WHERE tipo = ?
    // Ejemplo: obtener todos los reportes de RESERVAS
    List<Reporte> findByTipo(Reporte.TipoReporte tipo);

    // Busca todos los reportes generados por un usuario
    // SQL: SELECT * FROM reportes WHERE generado_por = ?
    // Ejemplo: ver todos los reportes que genero el admin 1
    List<Reporte> findByGeneradoPor(Long generadoPor);

    // Busca reportes de un tipo especifico generados
    // por un usuario especifico
    // SQL: SELECT * FROM reportes
    //      WHERE tipo = ? AND generado_por = ?
    List<Reporte> findByTipoAndGeneradoPor(
            Reporte.TipoReporte tipo, Long generadoPor);
}