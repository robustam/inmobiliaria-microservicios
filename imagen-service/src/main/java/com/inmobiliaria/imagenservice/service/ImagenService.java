
package com.inmobiliaria.imagenservice.service;

import com.inmobiliaria.imagenservice.dto.request.ImagenRequest;
import com.inmobiliaria.imagenservice.dto.response.ImagenResponse;
import com.inmobiliaria.imagenservice.model.Imagen;
import com.inmobiliaria.imagenservice.repository.ImagenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// ¿Para que sirve el paquete service?
// Contiene toda la logica de negocio de las imagenes
// Controla el limite de imagenes por entidad
// Guarda y elimina referencias de imagenes
@Service
@RequiredArgsConstructor
public class ImagenService {

    private static final Logger log =
            LoggerFactory.getLogger(ImagenService.class);

    // Repository para acceder a imagen_db
    private final ImagenRepository imagenRepository;

    // Limite maximo de imagenes por propiedad
    // Una propiedad puede tener maximo 10 fotos
    private static final Long LIMITE_IMAGENES = 10L;

    // ─────────────────────────────────────────────────
    // Registra una nueva imagen en el sistema
    // Verifica que no se supere el limite de imagenes
    // ─────────────────────────────────────────────────
    public ImagenResponse guardarImagen(ImagenRequest request) {

        log.info("Guardando imagen para entidad: {} tipo: {}",
                request.getEntidadId(),
                request.getTipoEntidad());

        try {
            // Verificamos que no se haya alcanzado
            // el limite de imagenes por entidad
            Long totalImagenes =
                    imagenRepository
                            .countByEntidadIdAndTipoEntidad(
                                    request.getEntidadId(),
                                    request.getTipoEntidad());

            if (totalImagenes >= LIMITE_IMAGENES) {
                log.warn("Limite de imagenes alcanzado: {}",
                        request.getEntidadId());
                throw new RuntimeException(
                        "Se alcanzo el limite maximo de " +
                                LIMITE_IMAGENES + " imagenes");
            }

            // Creamos la imagen con los datos del request
            Imagen imagen = new Imagen();
            imagen.setEntidadId(request.getEntidadId());
            imagen.setTipoEntidad(request.getTipoEntidad());
            imagen.setUrl(request.getUrl());
            imagen.setNombre(request.getNombre());

            // Guardamos en MySQL
            Imagen guardada = imagenRepository.save(imagen);

            log.info("Imagen guardada con id: {}",
                    guardada.getId());

            return convertirAResponse(guardada);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al guardar imagen: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al guardar la imagen");
        }
    }

    // ─────────────────────────────────────────────────
    // Lista todas las imagenes de una entidad
    // Ejemplo: todas las fotos de la propiedad 5
    // ─────────────────────────────────────────────────
    public List<ImagenResponse> listarPorEntidad(
            Long entidadId,
            Imagen.TipoEntidad tipoEntidad) {

        log.info("Listando imagenes → entidad: {} tipo: {}",
                entidadId, tipoEntidad);

        return imagenRepository
                .findByEntidadIdAndTipoEntidad(
                        entidadId, tipoEntidad)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────
    // Obtiene una imagen por su ID
    // ─────────────────────────────────────────────────
    public ImagenResponse obtenerImagen(Long id) {

        log.info("Buscando imagen con id: {}", id);

        Imagen imagen = imagenRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Imagen no encontrada: {}", id);
                    return new RuntimeException(
                            "Imagen no encontrada");
                });

        return convertirAResponse(imagen);
    }

    // ─────────────────────────────────────────────────
    // Elimina una imagen del sistema
    // ─────────────────────────────────────────────────
    public void eliminarImagen(Long id) {

        log.info("Eliminando imagen con id: {}", id);

        imagenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Imagen no encontrada"));

        imagenRepository.deleteById(id);

        log.info("Imagen eliminada: {}", id);
    }

    // ─────────────────────────────────────────────────
    // Convierte entidad a DTO — metodo privado
    // ─────────────────────────────────────────────────
    private ImagenResponse convertirAResponse(Imagen imagen) {
        return new ImagenResponse(
                imagen.getId(),
                imagen.getEntidadId(),
                imagen.getTipoEntidad(),
                imagen.getUrl(),
                imagen.getNombre(),
                imagen.getFechaCreacion()
        );
    }
}