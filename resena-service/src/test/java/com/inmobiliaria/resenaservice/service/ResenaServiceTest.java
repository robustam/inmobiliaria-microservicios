package com.inmobiliaria.resenaservice.service;

import com.inmobiliaria.resenaservice.client.PropiedadClient;
import com.inmobiliaria.resenaservice.exception.GlobalExceptionHandler.NegocioException;
import com.inmobiliaria.resenaservice.exception.GlobalExceptionHandler.RecursoNoEncontradoException;
import com.inmobiliaria.resenaservice.model.Resena;
import com.inmobiliaria.resenaservice.repository.ResenaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResenaService - Tests unitarios")
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private PropiedadClient propiedadClient;

    @InjectMocks
    private ResenaService resenaService;

    private Resena resenaSample;

    @BeforeEach
    void setUp() {
        resenaSample = Resena.builder()
                .id(1L)
                .propiedadId(10L)
                .usuarioId(5L)
                .nombreUsuario("Juan Pérez")
                .calificacion(4)
                .comentario("Muy buena propiedad, bien ubicada")
                .build();
    }

    @Test
    @DisplayName("findAll() retorna todas las reseñas")
    void findAll_retornaTodasLasResenas() {
        when(resenaRepository.findAll()).thenReturn(List.of(resenaSample));

        List<Resena> resultado = resenaService.findAll();

        assertThat(resultado).hasSize(1);
        verify(resenaRepository).findAll();
    }

    @Test
    @DisplayName("findById() retorna reseña cuando existe")
    void findById_retornaResenaExistente() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaSample));

        Resena resultado = resenaService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getCalificacion()).isEqualTo(4);
    }

    @Test
    @DisplayName("findById() lanza RecursoNoEncontradoException cuando no existe")
    void findById_lanzaExcepcionCuandoNoExiste() {
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resenaService.findById(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create() crea reseña con calificación válida")
    void create_creaResenaConCalificacionValida() {
        when(resenaRepository.save(any(Resena.class))).thenReturn(resenaSample);

        Resena resultado = resenaService.create(resenaSample);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(propiedadClient).findById(10L);
        verify(resenaRepository).save(resenaSample);
    }

    @Test
    @DisplayName("create() lanza NegocioException con calificación menor a 1")
    void create_lanzaExcepcionConCalificacionMenorA1() {
        resenaSample.setCalificacion(0);

        assertThatThrownBy(() -> resenaService.create(resenaSample))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("calificación");
    }

    @Test
    @DisplayName("create() lanza NegocioException con calificación mayor a 5")
    void create_lanzaExcepcionConCalificacionMayorA5() {
        resenaSample.setCalificacion(6);

        assertThatThrownBy(() -> resenaService.create(resenaSample))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("calificación");
    }

    @Test
    @DisplayName("update() actualiza calificación y comentario")
    void update_actualizaCalificacionYComentario() {
        Resena datosNuevos = Resena.builder()
                .calificacion(5)
                .comentario("Excelente propiedad, la recomiendo")
                .build();

        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaSample));
        when(resenaRepository.save(any(Resena.class))).thenReturn(resenaSample);

        Resena resultado = resenaService.update(1L, datosNuevos);

        assertThat(resultado).isNotNull();
        verify(resenaRepository).save(resenaSample);
    }

    @Test
    @DisplayName("getEstadisticasPropiedad() retorna estadísticas con promedio")
    void getEstadisticasPropiedad_retornaEstadisticasCorrectas() {
        when(resenaRepository.findByPropiedadId(10L)).thenReturn(List.of(resenaSample));
        when(resenaRepository.promedioCalificacion(10L)).thenReturn(4.0);

        Map<String, Object> estadisticas = resenaService.getEstadisticasPropiedad(10L);

        assertThat(estadisticas.get("totalResenas")).isEqualTo(1);
        assertThat(estadisticas.get("promedioCalificacion")).isEqualTo(4.0);
    }

    @Test
    @DisplayName("delete() elimina la reseña de la BD")
    void delete_eliminaResena() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaSample));

        resenaService.delete(1L);

        verify(resenaRepository).deleteById(1L);
    }
}