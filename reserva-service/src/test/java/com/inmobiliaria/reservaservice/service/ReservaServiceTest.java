package com.inmobiliaria.reservaservice.service;

import com.inmobiliaria.reservaservice.client.PropiedadClient;
import com.inmobiliaria.reservaservice.exception.GlobalExceptionHandler.NegocioException;
import com.inmobiliaria.reservaservice.exception.GlobalExceptionHandler.RecursoNoEncontradoException;
import com.inmobiliaria.reservaservice.model.Reserva;
import com.inmobiliaria.reservaservice.model.Reserva.EstadoReserva;
import com.inmobiliaria.reservaservice.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservaService - Tests unitarios")
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private PropiedadClient propiedadClient;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva reservaSample;
    private PropiedadClient.PropiedadDTO propiedadDisponible;

    @BeforeEach
    void setUp() {
        reservaSample = Reserva.builder()
                .id(1L)
                .propiedadId(10L)
                .usuarioId(5L)
                .fechaInicio(LocalDate.of(2025, 8, 1))
                .fechaFin(LocalDate.of(2025, 8, 31))
                .estado(EstadoReserva.PENDIENTE)
                .monto(new BigDecimal("500000"))
                .build();

        propiedadDisponible = new PropiedadClient.PropiedadDTO();
        propiedadDisponible.setId(10L);
        propiedadDisponible.setEstado("DISPONIBLE");
        propiedadDisponible.setPrecio(new BigDecimal("500000"));
    }

    @Test
    @DisplayName("findAll() retorna todas las reservas")
    void findAll_retornaTodasLasReservas() {
        when(reservaRepository.findAll()).thenReturn(List.of(reservaSample));

        List<Reserva> resultado = reservaService.findAll();

        assertThat(resultado).hasSize(1);
        verify(reservaRepository).findAll();
    }

    @Test
    @DisplayName("findById() retorna reserva cuando existe")
    void findById_retornaReservaExistente() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaSample));

        Reserva resultado = reservaService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getPropiedadId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("findById() lanza RecursoNoEncontradoException cuando no existe")
    void findById_lanzaExcepcionCuandoNoExiste() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.findById(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create() crea reserva cuando la propiedad está disponible")
    void create_creaReservaCuandoPropiedadDisponible() {
        when(propiedadClient.findById(10L)).thenReturn(propiedadDisponible);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaSample);

        Reserva resultado = reservaService.create(reservaSample);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(propiedadClient).cambiarEstado(10L, "ARRENDADA");
        verify(reservaRepository).save(reservaSample);
    }

    @Test
    @DisplayName("create() lanza NegocioException cuando la fecha de fin es anterior a la de inicio")
    void create_lanzaExcepcionConFechasInvalidas() {
        Reserva reservaInvalida = Reserva.builder()
                .propiedadId(10L)
                .usuarioId(5L)
                .fechaInicio(LocalDate.of(2025, 8, 31))
                .fechaFin(LocalDate.of(2025, 8, 1))
                .build();

        assertThatThrownBy(() -> reservaService.create(reservaInvalida))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("fecha de fin");
    }

    @Test
    @DisplayName("create() lanza NegocioException cuando la propiedad no está disponible")
    void create_lanzaExcepcionCuandoPropiedadNoDisponible() {
        PropiedadClient.PropiedadDTO propiedadArrendada = new PropiedadClient.PropiedadDTO();
        propiedadArrendada.setEstado("ARRENDADA");

        when(propiedadClient.findById(10L)).thenReturn(propiedadArrendada);

        assertThatThrownBy(() -> reservaService.create(reservaSample))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("no está disponible");
    }

    @Test
    @DisplayName("cambiarEstado() a CANCELADA libera la propiedad")
    void cambiarEstado_canceladaLiberaPropiedad() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaSample));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaSample);

        reservaService.cambiarEstado(1L, "CANCELADA");

        assertThat(reservaSample.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
        verify(propiedadClient).cambiarEstado(10L, "DISPONIBLE");
    }

    @Test
    @DisplayName("cambiarEstado() a CONFIRMADA no libera la propiedad")
    void cambiarEstado_confirmadaNoLiberaPropiedad() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaSample));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaSample);

        reservaService.cambiarEstado(1L, "CONFIRMADA");

        assertThat(reservaSample.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
        verify(propiedadClient, never()).cambiarEstado(anyLong(), anyString());
    }

    @Test
    @DisplayName("delete() elimina la reserva y libera la propiedad")
    void delete_eliminaReservaYLiberaPropiedad() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaSample));

        reservaService.delete(1L);

        verify(propiedadClient).cambiarEstado(10L, "DISPONIBLE");
        verify(reservaRepository).deleteById(1L);
    }
}