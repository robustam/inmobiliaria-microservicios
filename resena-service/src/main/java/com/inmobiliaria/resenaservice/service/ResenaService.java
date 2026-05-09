
package com.inmobiliaria.resenaservice.service;

import com.inmobiliaria.resenaservice.dto.request.ResenaRequest;
import com.inmobiliaria.resenaservice.dto.response.ResenaResponse;
import com.inmobiliaria.resenaservice.model.Resena;
import com.inmobiliaria.resenaservice.repository.ResenaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// ¿Para que sirve el paquete service?
// Contiene toda la logica de negocio de las resenas
// Ejemplo: verificar que el usuario no haya resenado antes
// Ejemplo: calcular el promedio de puntuacion
@Service
@RequiredArgsConstructor
public class ResenaService {

    private static final Logger log =
            LoggerFactory.getLogger(ResenaService.class);

    // Repository para acceder a resena_db
    private final ResenaRepository resenaRepository;

    // ─────────────────────────────────────────────────
    // Crea una nueva resena
    // Verifica que el usuario no haya resenado antes
    // ─────────────────────────────────────────────────
    public ResenaResponse crearResena(ResenaRequest request) {

        log.info("Creando resena para propiedad: {}",
                request.getPropiedadId());

        try {
            // Verificamos que el arrendatario no haya
            // resenado ya esta propiedad
            // Un usuario solo puede dejar una resena
            // por propiedad
            if (resenaRepository
                    .existsByPropiedadIdAndArrendatarioId(
                            request.getPropiedadId(),
                            request.getArrendatarioId())) {

                log.warn("Arrendatario {} ya reseno propiedad {}",
                        request.getArrendatarioId(),
                        request.getPropiedadId());

                throw new RuntimeException(
                        "Ya has resenado esta propiedad anteriormente");
            }

            // Creamos la resena con los datos del request
            Resena resena = new Resena();
            resena.setPropiedadId(request.getPropiedadId());
            resena.setArrendatarioId(
                    request.getArrendatarioId());
            resena.setPuntuacion(request.getPuntuacion());
            resena.setComentario(request.getComentario());

            // Guardamos en MySQL
            Resena guardada = resenaRepository.save(resena);

            log.info("Resena creada con id: {}",
                    guardada.getId());

            return convertirAResponse(guardada);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al crear resena: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al crear la resena");
        }
    }

    // ─────────────────────────────────────────────────
    // Lista todas las resenas de una propiedad
    // ─────────────────────────────────────────────────
    public List<ResenaResponse> listarPorPropiedad(
            Long propiedadId) {

        log.info("Listando resenas de propiedad: {}",
                propiedadId);

        return resenaRepository
                .findByPropiedadId(propiedadId)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────
    // Lista todas las resenas de un arrendatario
    // ─────────────────────────────────────────────────
    public List<ResenaResponse> listarPorArrendatario(
            Long arrendatarioId) {

        log.info("Listando resenas del arrendatario: {}",
                arrendatarioId);

        return resenaRepository
                .findByArrendatarioId(arrendatarioId)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────
    // Obtiene una resena por su ID
    // ─────────────────────────────────────────────────
    public ResenaResponse obtenerResena(Long id) {

        log.info("Buscando resena con id: {}", id);

        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Resena no encontrada: {}", id);
                    return new RuntimeException(
                            "Resena no encontrada");
                });

        return convertirAResponse(resena);
    }

    // ─────────────────────────────────────────────────
    // Calcula el promedio de puntuacion de una propiedad
    // Retorna el promedio como Double
    // Ejemplo: 4.5, 3.7, 5.0
    // ─────────────────────────────────────────────────
    public Double obtenerPromedio(Long propiedadId) {

        log.info("Calculando promedio de propiedad: {}",
                propiedadId);

        // Si no hay resenas retorna 0.0
        Double promedio = resenaRepository
                .calcularPromedioPuntuacion(propiedadId);

        return promedio != null ? promedio : 0.0;
    }

    // ─────────────────────────────────────────────────
    // Elimina una resena por su ID
    // ─────────────────────────────────────────────────
    public void eliminarResena(Long id) {

        log.info("Eliminando resena con id: {}", id);

        resenaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Resena no encontrada"));

        resenaRepository.deleteById(id);

        log.info("Resena eliminada: {}", id);
    }

    // ─────────────────────────────────────────────────
    // Convierte entidad a DTO — metodo privado
    // ─────────────────────────────────────────────────
    private ResenaResponse convertirAResponse(Resena resena) {
        return new ResenaResponse(
                resena.getId(),
                resena.getPropiedadId(),
                resena.getArrendatarioId(),
                resena.getPuntuacion(),
                resena.getComentario(),
                resena.getFechaCreacion()
        );
    }
}