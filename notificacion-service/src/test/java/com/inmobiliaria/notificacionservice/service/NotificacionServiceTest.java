package com.inmobiliaria.notificacionservice.service;

import com.inmobiliaria.notificacionservice.exception.GlobalExceptionHandler.RecursoNoEncontradoException;
import com.inmobiliaria.notificacionservice.model.Notificacion;
import com.inmobiliaria.notificacionservice.model.Notificacion.TipoNotificacion;
import com.inmobiliaria.notificacionservice.repository.NotificacionRepository;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacionService - Tests unitarios")
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    private Notificacion notificacionSample;

    @BeforeEach
    void setUp() {
        notificacionSample = Notificacion.builder()
                .id(1L)
                .usuarioId(5L)
                .titulo("Reserva confirmada")
                .mensaje("Tu reserva #10 ha sido confirmada exitosamente")
                .tipo(TipoNotificacion.RESERVA)
                .leida(false)
                .build();
    }

    @Test
    @DisplayName("findByUsuario() retorna notificaciones del usuario ordenadas")
    void findByUsuario_retornaNotificacionesDelUsuario() {
        when(notificacionRepository.findByUsuarioIdOrderByCreatedAtDesc(5L))
                .thenReturn(List.of(notificacionSample));

        List<Notificacion> resultado = notificacionService.findByUsuario(5L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsuarioId()).isEqualTo(5L);
        verify(notificacionRepository).findByUsuarioIdOrderByCreatedAtDesc(5L);
    }

    @Test
    @DisplayName("findNoLeidasByUsuario() retorna solo notificaciones no leídas")
    void findNoLeidasByUsuario_retornaSoloNoLeidas() {
        when(notificacionRepository.findByUsuarioIdAndLeidaFalseOrderByCreatedAtDesc(5L))
                .thenReturn(List.of(notificacionSample));

        List<Notificacion> resultado = notificacionService.findNoLeidasByUsuario(5L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).isLeida()).isFalse();
    }

    @Test
    @DisplayName("findById() retorna notificación cuando existe")
    void findById_retornaNotificacionExistente() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionSample));

        Notificacion resultado = notificacionService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getTitulo()).isEqualTo("Reserva confirmada");
    }

    @Test
    @DisplayName("findById() lanza RecursoNoEncontradoException cuando no existe")
    void findById_lanzaExcepcionCuandoNoExiste() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificacionService.findById(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create() guarda y retorna la notificación nueva")
    void create_guardaYRetornaNotificacion() {
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionSample);

        Notificacion resultado = notificacionService.create(notificacionSample);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(notificacionRepository).save(notificacionSample);
    }

    @Test
    @DisplayName("marcarLeida() actualiza leida=true y registra fecha")
    void marcarLeida_actualizaFlagYFecha() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionSample));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionSample);

        Notificacion resultado = notificacionService.marcarLeida(1L);

        assertThat(notificacionSample.isLeida()).isTrue();
        assertThat(notificacionSample.getLeidaAt()).isNotNull();
        verify(notificacionRepository).save(notificacionSample);
    }

    @Test
    @DisplayName("marcarTodasLeidas() marca todas las no leídas y retorna la cantidad")
    void marcarTodasLeidas_marcaTodasYRetornaCantidad() {
        Notificacion notificacion2 = Notificacion.builder()
                .id(2L).usuarioId(5L).titulo("Bienvenida").mensaje("Hola!")
                .tipo(TipoNotificacion.BIENVENIDA).leida(false).build();

        when(notificacionRepository.findByUsuarioIdAndLeidaFalseOrderByCreatedAtDesc(5L))
                .thenReturn(List.of(notificacionSample, notificacion2));
        when(notificacionRepository.saveAll(anyList())).thenReturn(List.of());

        int cantidad = notificacionService.marcarTodasLeidas(5L);

        assertThat(cantidad).isEqualTo(2);
        assertThat(notificacionSample.isLeida()).isTrue();
        assertThat(notificacion2.isLeida()).isTrue();
    }

    @Test
    @DisplayName("getResumenUsuario() retorna total y noLeidas correctamente")
    void getResumenUsuario_retornaResumenCorrecto() {
        when(notificacionRepository.findByUsuarioIdOrderByCreatedAtDesc(5L))
                .thenReturn(List.of(notificacionSample));
        when(notificacionRepository.countByUsuarioIdAndLeidaFalse(5L)).thenReturn(1L);

        Map<String, Object> resumen = notificacionService.getResumenUsuario(5L);

        assertThat(resumen.get("total")).isEqualTo(1L);
        assertThat(resumen.get("noLeidas")).isEqualTo(1L);
    }

    @Test
    @DisplayName("delete() elimina la notificación de la BD")
    void delete_eliminaNotificacion() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionSample));

        notificacionService.delete(1L);

        verify(notificacionRepository).deleteById(1L);
    }
}