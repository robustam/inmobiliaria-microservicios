package com.inmobiliaria.imagenservice.service;

import com.inmobiliaria.imagenservice.exception.GlobalExceptionHandler.RecursoNoEncontradoException;
import com.inmobiliaria.imagenservice.model.Imagen;
import com.inmobiliaria.imagenservice.repository.ImagenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImagenService - Tests unitarios")
class ImagenServiceTest {

    @Mock
    private ImagenRepository imagenRepository;

    @InjectMocks
    private ImagenService imagenService;

    private Imagen imagenSample;

    @BeforeEach
    void setUp() {
        imagenSample = Imagen.builder()
                .id(1L)
                .propiedadId(10L)
                .url("https://ejemplo.cl/fotos/casa-ñuñoa-1.jpg")
                .nombre("sala-comedor.jpg")
                .tipoMime("image/jpeg")
                .tamanioBytes(1024000L)
                .principal(false)
                .descripcion("Vista de la sala de estar")
                .build();
    }

    @Test
    @DisplayName("findAll() retorna todas las imágenes")
    void findAll_retornaTodasLasImagenes() {
        when(imagenRepository.findAll()).thenReturn(List.of(imagenSample));

        List<Imagen> resultado = imagenService.findAll();

        assertThat(resultado).hasSize(1);
        verify(imagenRepository).findAll();
    }

    @Test
    @DisplayName("findById() retorna imagen cuando existe")
    void findById_retornaImagenExistente() {
        when(imagenRepository.findById(1L)).thenReturn(Optional.of(imagenSample));

        Imagen resultado = imagenService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("sala-comedor.jpg");
    }

    @Test
    @DisplayName("findById() lanza RecursoNoEncontradoException cuando no existe")
    void findById_lanzaExcepcionCuandoNoExiste() {
        when(imagenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> imagenService.findById(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findByPropiedad() retorna imágenes ordenadas con principal primero")
    void findByPropiedad_retornaImagenesOrdenadas() {
        when(imagenRepository.findByPropiedadIdOrderByPrincipalDescCreatedAtAsc(10L))
                .thenReturn(List.of(imagenSample));

        List<Imagen> resultado = imagenService.findByPropiedad(10L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPropiedadId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("create() guarda imagen sin marcar anterior como no-principal cuando no es principal")
    void create_guardaImagenNoPrincipal() {
        when(imagenRepository.save(any(Imagen.class))).thenReturn(imagenSample);

        Imagen resultado = imagenService.create(imagenSample);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(imagenRepository, never()).findByPropiedadIdAndPrincipalTrue(any());
        verify(imagenRepository).save(imagenSample);
    }

    @Test
    @DisplayName("create() desactiva imagen principal anterior cuando la nueva es principal")
    void create_desactivaPrincipalAnteriorCuandoEsPrincipal() {
        imagenSample.setPrincipal(true);

        Imagen imagenPrincipalExistente = Imagen.builder()
                .id(2L).propiedadId(10L).url("antigua.jpg").principal(true).build();

        when(imagenRepository.findByPropiedadIdAndPrincipalTrue(10L))
                .thenReturn(Optional.of(imagenPrincipalExistente));
        when(imagenRepository.save(any(Imagen.class))).thenReturn(imagenSample);

        imagenService.create(imagenSample);

        assertThat(imagenPrincipalExistente.isPrincipal()).isFalse();
        verify(imagenRepository, times(2)).save(any(Imagen.class));
    }

    @Test
    @DisplayName("setPrincipal() establece la imagen como principal y desactiva la anterior")
    void setPrincipal_establecePrincipalYDesactivaAnterior() {
        Imagen imagenPrincipalExistente = Imagen.builder()
                .id(5L).propiedadId(10L).url("vieja.jpg").principal(true).build();

        when(imagenRepository.findById(1L)).thenReturn(Optional.of(imagenSample));
        when(imagenRepository.findByPropiedadIdAndPrincipalTrue(10L))
                .thenReturn(Optional.of(imagenPrincipalExistente));
        when(imagenRepository.save(any(Imagen.class))).thenReturn(imagenSample);

        imagenService.setPrincipal(1L);

        assertThat(imagenSample.isPrincipal()).isTrue();
        assertThat(imagenPrincipalExistente.isPrincipal()).isFalse();
    }

    @Test
    @DisplayName("delete() elimina la imagen de la BD")
    void delete_eliminaImagen() {
        when(imagenRepository.findById(1L)).thenReturn(Optional.of(imagenSample));

        imagenService.delete(1L);

        verify(imagenRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteByPropiedad() elimina todas las imágenes de una propiedad")
    void deleteByPropiedad_eliminaTodasLasImagenes() {
        when(imagenRepository.findByPropiedadIdOrderByPrincipalDescCreatedAtAsc(10L))
                .thenReturn(List.of(imagenSample));

        imagenService.deleteByPropiedad(10L);

        verify(imagenRepository).deleteAll(List.of(imagenSample));
    }
}