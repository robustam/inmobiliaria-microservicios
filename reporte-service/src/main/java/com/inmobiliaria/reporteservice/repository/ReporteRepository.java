package com.inmobiliaria.reporteservice.repository; // Paquete de acceso a datos

// ============================================================
// REPOSITORIO: REPORTE
// ============================================================
// Acceso a la tabla "reportes" en reporte_db.
// Guarda el historial de reportes generados por el sistema.
// ============================================================

import com.inmobiliaria.reporteservice.model.Reporte;
import com.inmobiliaria.reporteservice.model.Reporte.TipoReporte; // inner enum
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ReporteRepository extends CrudRepository<Reporte, Long> {

    // findByTipo(): reportes de un tipo específico.
    // SQL: SELECT * FROM reportes WHERE tipo = ?
    // Ejemplo: findByTipo(PROPIEDADES) retorna solo reportes de propiedades.
    List<Reporte> findByTipo(TipoReporte tipo);

    // findByGeneradoPor(): reportes generados por un administrador específico.
    // SQL: SELECT * FROM reportes WHERE generado_por = ?
    List<Reporte> findByGeneradoPor(Long usuarioId);

    // findAllByOrderByGeneradoEnDesc():
    //   findAll              = retorna todos los reportes
    //   OrderByGeneradoEnDesc = ORDER BY generado_en DESC (los más recientes primero)
    // SQL: SELECT * FROM reportes ORDER BY generado_en DESC
    List<Reporte> findAllByOrderByGeneradoEnDesc();
}