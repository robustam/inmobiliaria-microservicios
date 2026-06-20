package com.inmobiliaria.resenaservice.service; // Paquete de servicios

// ============================================================
// SERVICIO: RESEÑA - LÓGICA DE NEGOCIO
// ============================================================
// Gestiona las evaluaciones de propiedades.
//
// Responsabilidades:
//   - Listar reseñas (todas, por propiedad, por usuario)
//   - Calcular estadísticas (promedio de calificación, total)
//   - Crear nuevas reseñas (verifica que la propiedad exista via Feign)
//   - Actualizar reseñas existentes
//   - Eliminar reseñas
//
// INTEGRACIÓN: llama a propiedad-service via Feign al crear una reseña
// para verificar que la propiedad realmente existe en el sistema.
// ============================================================

import com.inmobiliaria.resenaservice.client.PropiedadClient;               // Feign client
import com.inmobiliaria.resenaservice.exception.GlobalExceptionHandler.NegocioException;            // Error 400
import com.inmobiliaria.resenaservice.exception.GlobalExceptionHandler.RecursoNoEncontradoException; // Error 404
import com.inmobiliaria.resenaservice.model.Resena;                         // Entidad
import com.inmobiliaria.resenaservice.repository.ResenaRepository;          // Acceso BD
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository resenaRepository; // acceso a tabla "resenas"
    private final PropiedadClient propiedadClient;   // cliente HTTP para propiedad-service

    // Retorna todas las reseñas del sistema.
    public List<Resena> findAll() {
        log.debug("Obteniendo todas las reseñas");
        return resenaRepository.findAll();
    }

    // Busca una reseña por ID. Lanza HTTP 404 si no existe.
    public Resena findById(Long id) {
        log.debug("Buscando reseña con id: {}", id);
        return resenaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reseña no encontrada con id: " + id));
    }

    // Retorna todas las reseñas de una propiedad específica.
    public List<Resena> findByPropiedad(Long propiedadId) {
        log.debug("Buscando reseñas de la propiedad: {}", propiedadId);
        return resenaRepository.findByPropiedadId(propiedadId);
    }

    // Retorna todas las reseñas escritas por un usuario específico.
    public List<Resena> findByUsuario(Long usuarioId) {
        log.debug("Buscando reseñas del usuario: {}", usuarioId);
        return resenaRepository.findByUsuarioId(usuarioId);
    }

    // Calcula estadísticas de las reseñas de una propiedad.
    // Retorna: { propiedadId, totalResenas, promedioCalificacion }
    public Map<String, Object> getEstadisticasPropiedad(Long propiedadId) {
        log.debug("Calculando estadísticas de propiedad: {}", propiedadId);
        List<Resena> resenas = findByPropiedad(propiedadId);
        // promedioCalificacion(): consulta JPQL con AVG(). Puede retornar null si no hay reseñas.
        Double promedio = resenaRepository.promedioCalificacion(propiedadId);
        // Map.of(): crea un mapa inmutable con las estadísticas.
        // Math.round(promedio * 10.0) / 10.0: redondea a 1 decimal (ej: 4.33 → 4.3).
        return Map.of(
                "propiedadId", propiedadId,
                "totalResenas", resenas.size(),
                "promedioCalificacion", promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0
        );
    }

    // Crea una nueva reseña con validaciones.
    // INTEGRACIÓN: verifica que la propiedad exista en propiedad-service.
    public Resena create(Resena resena) {
        log.info("Creando reseña para propiedad: {} por usuario: {}", resena.getPropiedadId(), resena.getUsuarioId());

        // Validación adicional de calificación (la anotación @Min/@Max también valida,
        // pero esta validación en el servicio da un mensaje más descriptivo).
        if (resena.getCalificacion() < 1 || resena.getCalificacion() > 5) {
            throw new NegocioException("La calificación debe ser entre 1 y 5");
        }

        // INTEGRACIÓN: llama a propiedad-service para verificar que la propiedad existe.
        // Si la propiedad no existe, propiedad-service retorna 404 y Feign lanza excepción.
        // Esto evita crear reseñas huérfanas (de propiedades inexistentes).
        propiedadClient.findById(resena.getPropiedadId());

        Resena guardada = resenaRepository.save(resena);
        log.info("Reseña creada con id: {} calificación: {}", guardada.getId(), guardada.getCalificacion());
        return guardada;
    }

    // Actualiza calificación y/o comentario de una reseña existente.
    // Solo actualiza los campos que vienen con valor.
    public Resena update(Long id, Resena datos) {
        log.info("Actualizando reseña con id: {}", id);
        Resena resena = findById(id); // lanza 404 si no existe

        if (datos.getCalificacion() != null) {
            // Valida la nueva calificación antes de actualizar.
            if (datos.getCalificacion() < 1 || datos.getCalificacion() > 5) {
                throw new NegocioException("La calificación debe ser entre 1 y 5");
            }
            resena.setCalificacion(datos.getCalificacion());
        }
        if (datos.getComentario() != null) resena.setComentario(datos.getComentario());

        return resenaRepository.save(resena); // UPDATE en la BD
    }

    // Elimina físicamente una reseña de la BD.
    public void delete(Long id) {
        log.info("Eliminando reseña con id: {}", id);
        findById(id); // lanza 404 si no existe
        // deleteById(): DELETE FROM resenas WHERE id = ?
        resenaRepository.deleteById(id);
    }
}