package com.inmobiliaria.imagenservice.service; // Paquete de servicios

// ============================================================
// SERVICIO: IMAGEN - LÓGICA DE NEGOCIO
// ============================================================
// Gestiona las imágenes/fotos de las propiedades del sistema.
//
// Responsabilidades:
//   - Listar imágenes (todas o por propiedad)
//   - Buscar imagen por ID o imagen principal de una propiedad
//   - Subir (registrar) nuevas imágenes
//   - Establecer una imagen como la foto principal
//   - Eliminar imágenes (una o todas las de una propiedad)
//
// Regla de negocio clave:
//   Solo puede haber UNA imagen principal por propiedad.
//   Al establecer una nueva como principal, la anterior pierde ese estado.
// ============================================================

import com.inmobiliaria.imagenservice.exception.RecursoNoEncontradoException; // Error 404
import com.inmobiliaria.imagenservice.model.Imagen;                           // Entidad
import com.inmobiliaria.imagenservice.repository.ImagenRepository;            // Acceso BD
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImagenService {

    private final ImagenRepository imagenRepository;

    // Retorna todas las imágenes del sistema.
    public List<Imagen> findAll() {
        log.debug("Obteniendo todas las imágenes");
        return imagenRepository.findAll();
    }

    // Busca una imagen por ID. Lanza HTTP 404 si no existe.
    public Imagen findById(Long id) {
        log.debug("Buscando imagen con id: {}", id);
        return imagenRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Imagen no encontrada con id: " + id));
    }

    // Retorna las imágenes de una propiedad: la principal primero, luego el resto.
    public List<Imagen> findByPropiedad(Long propiedadId) {
        log.debug("Obteniendo imágenes de la propiedad: {}", propiedadId);
        // OrderByPrincipalDescCreatedAtAsc: principal=true aparece primero.
        return imagenRepository.findByPropiedadIdOrderByPrincipalDescCreatedAtAsc(propiedadId);
    }

    // Busca la imagen marcada como principal de una propiedad.
    // Lanza HTTP 404 si la propiedad no tiene imagen principal configurada.
    public Imagen findPrincipalByPropiedad(Long propiedadId) {
        log.debug("Buscando imagen principal de la propiedad: {}", propiedadId);
        return imagenRepository.findByPropiedadIdAndPrincipalTrue(propiedadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay imagen principal para la propiedad: " + propiedadId));
    }

    // Registra una nueva imagen.
    // Si la nueva imagen es principal, desactiva la imagen principal anterior.
    public Imagen create(Imagen imagen) {
        log.info("Subiendo imagen para propiedad: {} - principal: {}", imagen.getPropiedadId(), imagen.isPrincipal());

        // Si esta imagen se marca como principal, la anterior debe dejar de serlo.
        if (imagen.isPrincipal()) {
            // ifPresent(): ejecuta el código solo si existe una imagen principal actual.
            imagenRepository.findByPropiedadIdAndPrincipalTrue(imagen.getPropiedadId())
                    .ifPresent(existente -> {
                        existente.setPrincipal(false); // quita el estado principal
                        imagenRepository.save(existente); // guarda el cambio
                    });
        }
        // Guarda la nueva imagen (con @PrePersist asigna createdAt).
        return imagenRepository.save(imagen);
    }

    // Establece una imagen como la foto principal de su propiedad.
    // Desactiva la principal anterior antes de activar la nueva.
    public Imagen setPrincipal(Long id) {
        log.info("Estableciendo imagen {} como principal", id);
        Imagen imagen = findById(id); // lanza 404 si no existe

        // Busca si ya hay una imagen principal para esta propiedad.
        imagenRepository.findByPropiedadIdAndPrincipalTrue(imagen.getPropiedadId())
                .ifPresent(existente -> {
                    // !existente.getId().equals(id): evita desactivar y reactivar la misma imagen.
                    if (!existente.getId().equals(id)) {
                        existente.setPrincipal(false);
                        imagenRepository.save(existente); // quita el estado de la anterior
                    }
                });

        imagen.setPrincipal(true); // activa la nueva imagen como principal
        return imagenRepository.save(imagen); // guarda el cambio
    }

    // Elimina UNA imagen de la BD.
    public void delete(Long id) {
        log.info("Eliminando imagen con id: {}", id);
        findById(id); // lanza 404 si no existe
        imagenRepository.deleteById(id); // DELETE FROM imagenes WHERE id = ?
    }

    // Elimina TODAS las imágenes de una propiedad.
    // Usado cuando se desactiva o elimina una propiedad del sistema.
    public void deleteByPropiedad(Long propiedadId) {
        log.info("Eliminando todas las imágenes de la propiedad: {}", propiedadId);
        List<Imagen> imagenes = findByPropiedad(propiedadId);
        // deleteAll(): elimina todos los registros de la lista en batch.
        imagenRepository.deleteAll(imagenes);
        log.debug("Se eliminaron {} imágenes de la propiedad: {}", imagenes.size(), propiedadId);
    }
}