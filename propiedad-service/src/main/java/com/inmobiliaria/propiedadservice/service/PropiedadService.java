package com.inmobiliaria.propiedadservice.service; // Paquete de servicios

// ============================================================
// SERVICIO: PROPIEDAD - LÓGICA DE NEGOCIO
// ============================================================
// Contiene TODA la lógica del negocio inmobiliario para propiedades.
// El Controller no toma decisiones: delega todo al Service.
//
// Responsabilidades:
//   - Consultar propiedades (todas, disponibles, por ID, por región, etc.)
//   - Crear nuevas propiedades
//   - Actualizar datos de una propiedad existente
//   - Cambiar el estado de una propiedad (DISPONIBLE, ARRENDADA, INACTIVA)
//   - "Eliminar" (en realidad cambia el estado a INACTIVA, no borra el registro)
// ============================================================

import com.inmobiliaria.propiedadservice.exception.GlobalExceptionHandler.NegocioException;             // Error 400
import com.inmobiliaria.propiedadservice.exception.GlobalExceptionHandler.RecursoNoEncontradoException; // Error 404
import com.inmobiliaria.propiedadservice.model.Propiedad;                        // Entidad
import com.inmobiliaria.propiedadservice.model.Propiedad.EstadoPropiedad;        // inner enum
import com.inmobiliaria.propiedadservice.model.Propiedad.TipoPropiedad;          // inner enum
import com.inmobiliaria.propiedadservice.repository.PropiedadRepository;         // Acceso BD
import lombok.RequiredArgsConstructor; // Constructor con campos final para inyección
import lombok.extern.slf4j.Slf4j;     // Activa el logger log.*
import org.springframework.stereotype.Service; // Marca como servicio Spring
import java.math.BigDecimal; // Para el parámetro de precio en búsqueda
import java.util.List;       // Tipo de retorno para listas de propiedades

// @Slf4j: genera: private static final Logger log = LoggerFactory.getLogger(PropiedadService.class);
@Slf4j
@Service
@RequiredArgsConstructor
public class PropiedadService {

    // propiedadRepository: inyectado por constructor.
    // Conecta con la tabla "propiedades" en propiedad_db de MySQL.
    private final PropiedadRepository propiedadRepository;

    // Retorna TODAS las propiedades sin importar estado.
    // Usado por el endpoint administrativo /todas.
    public List<Propiedad> findAll() {
        log.debug("Obteniendo todas las propiedades");
        return propiedadRepository.findAll();
    }

    // Retorna solo las propiedades con estado DISPONIBLE.
    // Usado por el endpoint público principal (lo que ven los arrendatarios).
    public List<Propiedad> findDisponibles() {
        log.debug("Obteniendo propiedades disponibles");
        // findByEstado(): filtro automático generado por Spring Data JPA.
        return propiedadRepository.findByEstado(EstadoPropiedad.DISPONIBLE);
    }

    // Busca una propiedad por su ID único.
    // Si no existe, lanza RecursoNoEncontradoException → HTTP 404.
    public Propiedad findById(Long id) {
        log.debug("Buscando propiedad con id: {}", id);
        // findById() retorna Optional<Propiedad>.
        // orElseThrow() lanza la excepción si el Optional está vacío.
        return propiedadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Propiedad no encontrada con id: " + id));
    }

    // Retorna todas las propiedades de un propietario específico.
    // Usado por los propietarios para ver sus propiedades publicadas.
    public List<Propiedad> findByPropietario(Long propietarioId) {
        log.debug("Buscando propiedades del propietario: {}", propietarioId);
        return propiedadRepository.findByPropietarioId(propietarioId);
    }

    // Busca propiedades DISPONIBLES en una región específica.
    // "IgnoreCase" = no importa si dice "metropolitana" o "Metropolitana".
    public List<Propiedad> findByRegion(String region) {
        log.debug("Buscando propiedades en región: {}", region);
        return propiedadRepository.findByRegionIgnoreCaseAndEstado(region, EstadoPropiedad.DISPONIBLE);
    }

    // Busca propiedades DISPONIBLES en una comuna específica.
    public List<Propiedad> findByComuna(String comuna) {
        log.debug("Buscando propiedades en comuna: {}", comuna);
        return propiedadRepository.findByComunaIgnoreCaseAndEstado(comuna, EstadoPropiedad.DISPONIBLE);
    }

    // Búsqueda avanzada con múltiples filtros opcionales.
    // Todos los parámetros son opcionales: si son null, se ignoran en la consulta.
    public List<Propiedad> buscar(String region, String ciudad, String comuna, String tipo,
                                   BigDecimal precioMin, BigDecimal precioMax) {
        // log.debug() con múltiples parámetros: {} se reemplazan en orden.
        log.debug("Búsqueda avanzada - region:{} ciudad:{} comuna:{} tipo:{} precioMin:{} precioMax:{}",
                region, ciudad, comuna, tipo, precioMin, precioMax);

        // Convertir el String "CASA"/"DEPARTAMENTO" al enum TipoPropiedad.
        // Si tipo es null o vacío, tipoEnum queda null (la consulta ignorará este filtro).
        TipoPropiedad tipoEnum = null;
        if (tipo != null && !tipo.isBlank()) {
            // valueOf(): convierte un String al enum correspondiente.
            // toUpperCase(): acepta "casa", "CASA", "Casa" → todos válidos.
            // Si el valor no existe en el enum, lanza IllegalArgumentException →
            // GlobalExceptionHandler → HTTP 400.
            tipoEnum = TipoPropiedad.valueOf(tipo.toUpperCase());
        }
        // Llama a la consulta JPQL personalizada en el repositorio.
        return propiedadRepository.buscar(region, ciudad, comuna, tipoEnum, precioMin, precioMax);
    }

    // Crea y guarda una nueva propiedad en la BD.
    // @PrePersist en la entidad asigna automáticamente createdAt, updatedAt,
    // estado DISPONIBLE y moneda CLP si no se especificaron.
    public Propiedad create(Propiedad propiedad) {
        log.info("Creando propiedad: {} - {}", propiedad.getTipo(), propiedad.getTitulo());
        // save() ejecuta INSERT en la tabla propiedades.
        // Retorna la entidad guardada (con el ID asignado por MySQL).
        return propiedadRepository.save(propiedad);
    }

    // Actualiza los datos de una propiedad existente.
    // Solo actualiza campos de contenido, NO el estado ni el propietario.
    public Propiedad update(Long id, Propiedad datos) {
        log.info("Actualizando propiedad con id: {}", id);
        // Primero busca la propiedad (lanza 404 si no existe).
        Propiedad propiedad = findById(id);

        // Actualiza campo por campo con los nuevos valores recibidos.
        propiedad.setTitulo(datos.getTitulo());
        propiedad.setDescripcion(datos.getDescripcion());
        propiedad.setPrecio(datos.getPrecio());
        propiedad.setRegion(datos.getRegion());
        propiedad.setCiudad(datos.getCiudad());
        propiedad.setComuna(datos.getComuna());
        propiedad.setDireccion(datos.getDireccion());
        propiedad.setHabitaciones(datos.getHabitaciones());
        propiedad.setBanos(datos.getBanos());
        propiedad.setMetrosCuadrados(datos.getMetrosCuadrados());
        propiedad.setTipo(datos.getTipo());
        // Moneda solo se actualiza si el request trae un valor (protege el default "CLP").
        if (datos.getMoneda() != null) propiedad.setMoneda(datos.getMoneda());

        // save() en una entidad con ID existente ejecuta UPDATE (no INSERT).
        // @PreUpdate en la entidad actualiza updatedAt automáticamente.
        return propiedadRepository.save(propiedad);
    }

    // Cambia el estado de una propiedad (DISPONIBLE, ARRENDADA, INACTIVA).
    // Llamado por reserva-service cuando se confirma/cancela un arriendo.
    public Propiedad cambiarEstado(Long id, String estado) {
        log.info("Cambiando estado de propiedad {} a {}", id, estado);
        Propiedad propiedad = findById(id); // lanza 404 si no existe

        // valueOf() convierte el String al enum.
        // Si el estado no es válido, lanza IllegalArgumentException → HTTP 400.
        propiedad.setEstado(EstadoPropiedad.valueOf(estado.toUpperCase()));
        return propiedadRepository.save(propiedad);
    }

    // "Elimina" (desactiva) una propiedad cambiando su estado a INACTIVA.
    // NO se borra el registro de la BD (borrado lógico, no físico).
    // Esto conserva el historial de reservas y reseñas asociadas.
    public void delete(Long id) {
        log.info("Desactivando propiedad con id: {}", id);
        Propiedad propiedad = findById(id); // lanza 404 si no existe
        propiedad.setEstado(EstadoPropiedad.INACTIVA); // cambia estado a inactiva
        propiedadRepository.save(propiedad); // guarda el cambio
    }
}