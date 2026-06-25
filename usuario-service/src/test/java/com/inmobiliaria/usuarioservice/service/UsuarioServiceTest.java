package com.inmobiliaria.usuarioservice.service;

import com.inmobiliaria.usuarioservice.exception.GlobalExceptionHandler.NegocioException;
import com.inmobiliaria.usuarioservice.exception.GlobalExceptionHandler.RecursoNoEncontradoException;
import com.inmobiliaria.usuarioservice.model.Usuario;
import com.inmobiliaria.usuarioservice.repository.UsuarioRepository;
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
@DisplayName("UsuarioService - Tests unitarios")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioSample;

    @BeforeEach
    void setUp() {
        usuarioSample = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@mail.cl")
                .telefono("912345678")
                .ciudad("Santiago")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("findAll() retorna solo usuarios activos")
    void findAll_retornaSoloUsuariosActivos() {
        when(usuarioRepository.findByActivoTrue()).thenReturn(List.of(usuarioSample));

        List<Usuario> resultado = usuarioService.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).isActivo()).isTrue();
        verify(usuarioRepository).findByActivoTrue();
    }

    @Test
    @DisplayName("findById() retorna usuario cuando existe")
    void findById_retornaUsuarioExistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioSample));

        Usuario resultado = usuarioService.findById(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("findById() lanza RecursoNoEncontradoException cuando no existe")
    void findById_lanzaExcepcionCuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.findById(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findByEmail() retorna usuario cuando el email existe")
    void findByEmail_retornaUsuarioPorEmail() {
        when(usuarioRepository.findByEmail("juan@mail.cl")).thenReturn(Optional.of(usuarioSample));

        Usuario resultado = usuarioService.findByEmail("juan@mail.cl");

        assertThat(resultado.getEmail()).isEqualTo("juan@mail.cl");
    }

    @Test
    @DisplayName("findByEmail() lanza RecursoNoEncontradoException cuando el email no existe")
    void findByEmail_lanzaExcepcionCuandoEmailNoExiste() {
        when(usuarioRepository.findByEmail("noexiste@mail.cl")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.findByEmail("noexiste@mail.cl"))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("noexiste@mail.cl");
    }

    @Test
    @DisplayName("create() guarda y retorna el nuevo usuario")
    void create_guardaYRetornaUsuario() {
        when(usuarioRepository.existsByEmail("juan@mail.cl")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSample);

        Usuario resultado = usuarioService.create(usuarioSample);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(usuarioRepository).save(usuarioSample);
    }

    @Test
    @DisplayName("create() lanza NegocioException cuando el email ya está registrado")
    void create_lanzaExcepcionConEmailDuplicado() {
        when(usuarioRepository.existsByEmail("juan@mail.cl")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.create(usuarioSample))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("email ya está registrado");
    }

    @Test
    @DisplayName("update() actualiza los campos del perfil")
    void update_actualizaCamposDelPerfil() {
        Usuario datosNuevos = Usuario.builder()
                .nombre("Juan Actualizado")
                .apellido("Pérez Nuevo")
                .telefono("987654321")
                .ciudad("Valparaíso")
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioSample));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSample);

        Usuario resultado = usuarioService.update(1L, datosNuevos);

        assertThat(resultado).isNotNull();
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("delete() desactiva el usuario (borrado lógico)")
    void delete_desactivaUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioSample));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSample);

        usuarioService.delete(1L);

        assertThat(usuarioSample.isActivo()).isFalse();
        verify(usuarioRepository).save(usuarioSample);
    }

    @Test
    @DisplayName("findByCiudad() retorna usuarios activos de la ciudad")
    void findByCiudad_retornaUsuariosDeLaCiudad() {
        when(usuarioRepository.findByCiudadIgnoreCaseAndActivoTrue("Santiago"))
                .thenReturn(List.of(usuarioSample));

        List<Usuario> resultado = usuarioService.findByCiudad("Santiago");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCiudad()).isEqualTo("Santiago");
    }
}