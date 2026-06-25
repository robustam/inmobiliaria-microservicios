package com.inmobiliaria.reservaservice.scheduler;

import com.inmobiliaria.reservaservice.client.PropiedadClient;
import com.inmobiliaria.reservaservice.model.Reserva;
import com.inmobiliaria.reservaservice.model.Reserva.EstadoReserva;
import com.inmobiliaria.reservaservice.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservaScheduler - Tests unitarios")
class ReservaSchedulerTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private PropiedadClient propiedadClient;

    @InjectMocks
    private ReservaScheduler reservaScheduler;

    private Reserva reservaVencidaConfirmada;
    private Reserva reservaVencidaPendiente;

    @BeforeEach
    void setUp() {
        // Reserva CONFIRMADA cuya fechaFin ya pasó → debe liberarse
        reservaVencidaConfirmada = Reserva.builder()
                .id(1L)
                .propiedadId(10L)
                .usuarioId(5L)
                .fechaInicio(LocalDate.now().minusDays(30))
                .fechaFin(LocalDate.now().minusDays(1))
                .estado(EstadoReserva.CONFIRMADA)
                .monto(new BigDecimal("500000"))
                .build();

        // Reserva PENDIENTE cuya fechaFin también pasó → también debe liberarse
        reservaVencidaPendiente = Reserva.builder()
                .id(2L)
                .propiedadId(20L)
                .usuarioId(7L)
                .fechaInicio(LocalDate.now().minusDays(15))
                .fechaFin(LocalDate.now().minusDays(1))
                .estado(EstadoReserva.PENDIENTE)
                .monto(new BigDecimal("300000"))
                .build();
    }

    @Test
    @DisplayName("liberarPropiedadesVencidas() completa reservas y libera propiedades cuando hay vencidas")
    void liberarPropiedadesVencidas_procesaReservasVencidas() {
        when(reservaRepository.findByEstadoInAndFechaFinBefore(
                anyList(), any(LocalDate.class)))
                .thenReturn(List.of(reservaVencidaConfirmada, reservaVencidaPendiente));

        reservaScheduler.liberarPropiedadesVencidas();

        // Verifica que ambas reservas se guardaron como COMPLETADA
        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepository, times(2)).save(captor.capture());

        List<Reserva> guardadas = captor.getAllValues();
        assertThat(guardadas).allMatch(r -> r.getEstado() == EstadoReserva.COMPLETADA);

        // Verifica que se liberaron las dos propiedades
        verify(propiedadClient).cambiarEstado(10L, "DISPONIBLE");
        verify(propiedadClient).cambiarEstado(20L, "DISPONIBLE");
    }

    @Test
    @DisplayName("liberarPropiedadesVencidas() no hace nada cuando no hay reservas vencidas")
    void liberarPropiedadesVencidas_noHaceNadaSinVencidas() {
        when(reservaRepository.findByEstadoInAndFechaFinBefore(
                anyList(), any(LocalDate.class)))
                .thenReturn(List.of());

        reservaScheduler.liberarPropiedadesVencidas();

        verify(reservaRepository, never()).save(any());
        verify(propiedadClient, never()).cambiarEstado(anyLong(), anyString());
    }

    @Test
    @DisplayName("liberarPropiedadesVencidas() cambia el estado de la reserva a COMPLETADA")
    void liberarPropiedadesVencidas_cambiaEstadoACompletada() {
        when(reservaRepository.findByEstadoInAndFechaFinBefore(
                anyList(), any(LocalDate.class)))
                .thenReturn(List.of(reservaVencidaConfirmada));

        reservaScheduler.liberarPropiedadesVencidas();

        assertThat(reservaVencidaConfirmada.getEstado()).isEqualTo(EstadoReserva.COMPLETADA);
    }

    @Test
    @DisplayName("liberarPropiedadesVencidas() continúa con las demás reservas si una falla")
    void liberarPropiedadesVencidas_continuaSiUnaFalla() {
        when(reservaRepository.findByEstadoInAndFechaFinBefore(
                anyList(), any(LocalDate.class)))
                .thenReturn(List.of(reservaVencidaConfirmada, reservaVencidaPendiente));

        // La primera llamada a propiedadClient lanza excepción (ej: servicio caído)
        doThrow(new RuntimeException("propiedad-service no disponible"))
                .when(propiedadClient).cambiarEstado(10L, "DISPONIBLE");

        // No debe lanzar excepción hacia afuera
        reservaScheduler.liberarPropiedadesVencidas();

        // La segunda reserva sí debe procesarse aunque la primera falló
        verify(propiedadClient).cambiarEstado(20L, "DISPONIBLE");
        verify(reservaRepository, times(2)).save(any(Reserva.class));
    }

    @Test
    @DisplayName("liberarPropiedadesVencidas() consulta solo estados PENDIENTE y CONFIRMADA")
    void liberarPropiedadesVencidas_consultaEstadosCorrectos() {
        when(reservaRepository.findByEstadoInAndFechaFinBefore(
                anyList(), any(LocalDate.class)))
                .thenReturn(List.of());

        reservaScheduler.liberarPropiedadesVencidas();

        ArgumentCaptor<List<EstadoReserva>> estadosCaptor = ArgumentCaptor.forClass(List.class);
        verify(reservaRepository).findByEstadoInAndFechaFinBefore(
                estadosCaptor.capture(), any(LocalDate.class));

        List<EstadoReserva> estadosConsultados = estadosCaptor.getValue();
        assertThat(estadosConsultados)
                .containsExactlyInAnyOrder(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA)
                .doesNotContain(EstadoReserva.CANCELADA, EstadoReserva.COMPLETADA);
    }

    @Test
    @DisplayName("liberarPropiedadesVencidas() consulta con la fecha de hoy")
    void liberarPropiedadesVencidas_consultaConFechaHoy() {
        when(reservaRepository.findByEstadoInAndFechaFinBefore(
                anyList(), any(LocalDate.class)))
                .thenReturn(List.of());

        LocalDate antesDeEjecutar = LocalDate.now();
        reservaScheduler.liberarPropiedadesVencidas();
        LocalDate despuesDeEjecutar = LocalDate.now();

        ArgumentCaptor<LocalDate> fechaCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(reservaRepository).findByEstadoInAndFechaFinBefore(anyList(), fechaCaptor.capture());

        LocalDate fechaUsada = fechaCaptor.getValue();
        assertThat(fechaUsada).isBetween(antesDeEjecutar, despuesDeEjecutar);
    }
}