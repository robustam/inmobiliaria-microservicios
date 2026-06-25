package com.inmobiliaria.propiedadservice.service;

import com.inmobiliaria.propiedadservice.exception.GlobalExceptionHandler.NegocioException;
import com.inmobiliaria.propiedadservice.exception.GlobalExceptionHandler.RecursoNoEncontradoException;
import com.inmobiliaria.propiedadservice.model.Propiedad;
import com.inmobiliaria.propiedadservice.model.Propiedad.EstadoPropiedad;
import com.inmobiliaria.propiedadservice.model.Propiedad.TipoPropiedad;
import com.inmobiliaria.propiedadservice.repository.PropiedadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PropiedadService - Tests unitarios")
class PropiedadServiceTest {

    @Mock
    private PropiedadRepository propiedadRepository;

    @InjectMocks
    private PropiedadService propiedadService;

    private Propiedad propiedadSample;

    @BeforeEach
    void setUp() {
        propiedadSample = Propiedad.builder()
                .id(1L)
                .titulo("Casa en Ñuñoa")
                .descripcion("Casa amplia con jardín")
                .precio(new BigDecimal("500000"))
                .moneda("CLP")
                .region("Región Metropolitana")
                .ciudad("Santiago")
                .comuna("Ñuñoa")
                .habitaciones(3)
                .banos(2)
                .metrosCuadrados(120.0)
                .tipo(TipoPropiedad.CASA)
                .estado(EstadoPropiedad.DISPONIBLE)
                .propietarioId(10L)
                .build();
    }

    @Test
    @DisplayName("findAll() retorna todas las propiedades")
    void findAll_retornaListaCompleta() {
        when(propiedadRepository.findAll()).thenReturn(List.of(propiedadSample));

        List<Propiedad> resultado = propiedadService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTitulo()).isEqualTo("Casa en Ñuñoa");
        verify(propiedadRepository).findAll();
    }

    @Test
    @DisplayName("findDisponibles() retorna solo propiedades con estado DISPONIBLE")
    void findDisponibles_retornaSoloDisponibles() {
        when(propiedadRepository.findByEstado(EstadoPropiedad.DISPONIBLE))
                .thenReturn(List.of(propiedadSample));

        List<Propiedad> resultado = propiedadService.findDisponibles();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoPropiedad.DISPONIBLE);
        verify(propiedadRepository).findByEstado(EstadoPropiedad.DISPONIBLE);
    }

    @Test
    @DisplayName("findById() retorna propiedad cuando existe")
    void findById_retornaPropiedadExistente() {
        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedadSample));

        Propiedad resultado = propiedadService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getTitulo()).isEqualTo("Casa en Ñuñoa");
    }

    @Test
    @DisplayName("findById() lanza RecursoNoEncontradoException cuando no existe")
    void findById_lanzaExcepcionCuandoNoExiste() {
        when(propiedadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propiedadService.findById(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create() guarda y retorna la propiedad nueva")
    void create_guardaYRetornaPropiedad() {
        when(propiedadRepository.save(any(Propiedad.class))).thenReturn(propiedadSample);

        Propiedad resultado = propiedadService.create(propiedadSample);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(propiedadRepository).save(propiedadSample);
    }

    @Test
    @DisplayName("update() actualiza los campos y guarda la propiedad")
    void update_actualizaCamposYGuarda() {
        Propiedad datosNuevos = Propiedad.builder()
                .titulo("Casa renovada en Ñuñoa")
                .descripcion("Casa amplia renovada")
                .precio(new BigDecimal("600000"))
                .region("Región Metropolitana")
                .ciudad("Santiago")
                .comuna("Ñuñoa")
                .habitaciones(4)
                .banos(3)
                .metrosCuadrados(150.0)
                .tipo(TipoPropiedad.CASA)
                .build();

        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedadSample));
        when(propiedadRepository.save(any(Propiedad.class))).thenReturn(propiedadSample);

        Propiedad resultado = propiedadService.update(1L, datosNuevos);

        assertThat(resultado).isNotNull();
        verify(propiedadRepository).save(any(Propiedad.class));
    }

    @Test
    @DisplayName("cambiarEstado() actualiza el estado de la propiedad")
    void cambiarEstado_actualizaEstado() {
        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedadSample));
        when(propiedadRepository.save(any(Propiedad.class))).thenReturn(propiedadSample);

        Propiedad resultado = propiedadService.cambiarEstado(1L, "ARRENDADA");

        assertThat(propiedadSample.getEstado()).isEqualTo(EstadoPropiedad.ARRENDADA);
        verify(propiedadRepository).save(propiedadSample);
    }

    @Test
    @DisplayName("cambiarEstado() lanza IllegalArgumentException con estado inválido")
    void cambiarEstado_lanzaExcepcionConEstadoInvalido() {
        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedadSample));

        assertThatThrownBy(() -> propiedadService.cambiarEstado(1L, "ESTADO_INEXISTENTE"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("delete() cambia el estado a INACTIVA (borrado lógico)")
    void delete_cambiaEstadoAInactiva() {
        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedadSample));
        when(propiedadRepository.save(any(Propiedad.class))).thenReturn(propiedadSample);

        propiedadService.delete(1L);

        assertThat(propiedadSample.getEstado()).isEqualTo(EstadoPropiedad.INACTIVA);
        verify(propiedadRepository).save(propiedadSample);
    }

    @Test
    @DisplayName("buscar() con tipo válido retorna lista de propiedades")
    void buscar_conTipoValidoRetornaLista() {
        when(propiedadRepository.buscar(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(propiedadSample));

        List<Propiedad> resultado = propiedadService.buscar(
                "Región Metropolitana", "Santiago", "Ñuñoa", "CASA",
                new BigDecimal("100000"), new BigDecimal("700000"));

        assertThat(resultado).hasSize(1);
    }
}