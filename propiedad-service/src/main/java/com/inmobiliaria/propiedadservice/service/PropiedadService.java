package com.inmobiliaria.propiedadservice.service;

import com.inmobiliaria.propiedadservice.dto.request.PropiedadRequest;
import com.inmobiliaria.propiedadservice.dto.response.PropiedadResponse;
import com.inmobiliaria.propiedadservice.model.Propiedad;
import com.inmobiliaria.propiedadservice.repository.PropiedadRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// ¿Para que sirve el paquete service?
// Contiene toda la logica de negocio del microservicio
// El controller recibe la peticion y la delega al service
// El service procesa los datos y retorna el resultado
// NUNCA mezclar logica de negocio con el controller
@Service
@RequiredArgsConstructor
public class PropiedadService {

    private static final Logger log =
            LoggerFactory.getLogger(PropiedadService.class);

    private final PropiedadRepository propiedadRepository;

    // Crea una nueva propiedad en el sistema
    public PropiedadResponse crearPropiedad(
            PropiedadRequest request) {

        log.info("Creando propiedad para arrendador: {}",
                request.getArrendadorId());

        try {
            // Creamos la entidad con los datos del request
            Propiedad propiedad = new Propiedad();
            propiedad.setArrendadorId(request.getArrendadorId());
            propiedad.setTitulo(request.getTitulo());
            propiedad.setDescripcion(request.getDescripcion());
            propiedad.setDireccion(request.getDireccion());
            propiedad.setComuna(request.getComuna());
            propiedad.setCiudad(request.getCiudad());
            propiedad.setPrecioMensual(request.getPrecioMensual());
            propiedad.setHabitaciones(request.getHabitaciones());
            propiedad.setBanios(request.getBanios());
            propiedad.setM2(request.getM2());

            // Por defecto la propiedad nace disponible
            propiedad.setDisponible(true);

            // Guardamos en MySQL
            Propiedad guardada =
                    propiedadRepository.save(propiedad);

            log.info("Propiedad creada con id: {}",
                    guardada.getId());

            return convertirAResponse(guardada);

        } catch (Exception e) {
            log.error("Error al crear propiedad: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al crear la propiedad");
        }
    }

    // Obtiene una propiedad por su ID
    public PropiedadResponse obtenerPropiedad(Long id) {

        log.info("Buscando propiedad con id: {}", id);

        Propiedad propiedad = propiedadRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Propiedad no encontrada: {}", id);
                    return new RuntimeException(
                            "Propiedad no encontrada");
                });

        return convertirAResponse(propiedad);
    }

    // Lista todas las propiedades del sistema
    public List<PropiedadResponse> listarPropiedades() {

        log.info("Listando todas las propiedades");

        return propiedadRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // Lista propiedades de un arrendador especifico
    public List<PropiedadResponse> listarPorArrendador(
            Long arrendadorId) {

        log.info("Listando propiedades del arrendador: {}",
                arrendadorId);

        return propiedadRepository
                .findByArrendadorId(arrendadorId)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // Lista solo propiedades disponibles
    public List<PropiedadResponse> listarDisponibles() {

        log.info("Listando propiedades disponibles");

        return propiedadRepository
                .findByDisponible(true)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    // Actualiza los datos de una propiedad existente
    public PropiedadResponse actualizarPropiedad(
            Long id, PropiedadRequest request) {

        log.info("Actualizando propiedad con id: {}", id);

        Propiedad propiedad = propiedadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Propiedad no encontrada"));

        // Actualizamos todos los campos
        propiedad.setTitulo(request.getTitulo());
        propiedad.setDescripcion(request.getDescripcion());
        propiedad.setDireccion(request.getDireccion());
        propiedad.setComuna(request.getComuna());
        propiedad.setCiudad(request.getCiudad());
        propiedad.setPrecioMensual(request.getPrecioMensual());
        propiedad.setHabitaciones(request.getHabitaciones());
        propiedad.setBanios(request.getBanios());
        propiedad.setM2(request.getM2());

        Propiedad actualizada =
                propiedadRepository.save(propiedad);

        log.info("Propiedad actualizada: {}", id);

        return convertirAResponse(actualizada);
    }

    // Cambia la disponibilidad de una propiedad
    // true = disponible, false = no disponible
    public PropiedadResponse cambiarDisponibilidad(
            Long id, Boolean disponible) {

        log.info("Cambiando disponibilidad de propiedad: {}",
                id);

        Propiedad propiedad = propiedadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Propiedad no encontrada"));

        propiedad.setDisponible(disponible);
        Propiedad actualizada =
                propiedadRepository.save(propiedad);

        log.info("Disponibilidad cambiada a: {} para propiedad: {}",
                disponible, id);

        return convertirAResponse(actualizada);
    }

    // Elimina una propiedad del sistema
    public void eliminarPropiedad(Long id) {

        log.info("Eliminando propiedad con id: {}", id);

        propiedadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Propiedad no encontrada"));

        propiedadRepository.deleteById(id);

        log.info("Propiedad eliminada: {}", id);
    }

    // Convierte entidad a DTO de respuesta
    // Metodo privado — solo lo usa esta clase
    private PropiedadResponse convertirAResponse(
            Propiedad propiedad) {
        return new PropiedadResponse(
                propiedad.getId(),
                propiedad.getArrendadorId(),
                propiedad.getTitulo(),
                propiedad.getDescripcion(),
                propiedad.getDireccion(),
                propiedad.getComuna(),
                propiedad.getCiudad(),
                propiedad.getPrecioMensual(),
                propiedad.getHabitaciones(),
                propiedad.getBanios(),
                propiedad.getM2(),
                propiedad.getDisponible()
        );
    }
}