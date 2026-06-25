package com.inmobiliaria.authservice.service;

import com.inmobiliaria.authservice.dto.LoginResponse;
import com.inmobiliaria.authservice.dto.RegisterRequest;
import com.inmobiliaria.authservice.dto.TokenValidationResponse;
import com.inmobiliaria.authservice.exception.GlobalExceptionHandler.NegocioException;
import com.inmobiliaria.authservice.exception.GlobalExceptionHandler.RecursoNoEncontradoException;
import com.inmobiliaria.authservice.model.Usuario;
import com.inmobiliaria.authservice.model.Usuario.Role;
import com.inmobiliaria.authservice.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Tests unitarios")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioSample;

    @BeforeEach
    void setUp() {
        usuarioSample = Usuario.builder()
                .id(1L)
                .username("juan123")
                .email("juan@mail.cl")
                .password("$2a$10$hashedPassword")
                .nombre("Juan Pérez")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("login() retorna LoginResponse con token cuando las credenciales son correctas")
    void login_retornaTokenConCredencialesCorrectas() {
        when(usuarioRepository.findByUsername("juan123")).thenReturn(Optional.of(usuarioSample));
        when(passwordEncoder.matches("miPass123", usuarioSample.getPassword())).thenReturn(true);
        when(jwtService.generateToken("juan123", "USER")).thenReturn("eyJhbGc.token.firma");

        LoginResponse respuesta = authService.login("juan123", "miPass123");

        assertThat(respuesta.getToken()).isEqualTo("eyJhbGc.token.firma");
        assertThat(respuesta.getUsername()).isEqualTo("juan123");
        assertThat(respuesta.getRole()).isEqualTo("USER");
        assertThat(respuesta.getMessage()).isEqualTo("Login exitoso");
    }

    @Test
    @DisplayName("login() lanza RecursoNoEncontradoException cuando el usuario no existe")
    void login_lanzaExcepcionCuandoUsuarioNoExiste() {
        when(usuarioRepository.findByUsername("noExiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("noExiste", "cualquierPass"))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("no encontrado");
    }

    @Test
    @DisplayName("login() lanza NegocioException cuando la contraseña es incorrecta")
    void login_lanzaExcepcionConContrasenaIncorrecta() {
        when(usuarioRepository.findByUsername("juan123")).thenReturn(Optional.of(usuarioSample));
        when(passwordEncoder.matches("passIncorrecta", usuarioSample.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login("juan123", "passIncorrecta"))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("Credenciales incorrectas");
    }

    @Test
    @DisplayName("register() crea el usuario y retorna token JWT")
    void register_creaUsuarioYRetornaToken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("nuevousuario");
        request.setEmail("nuevo@mail.cl");
        request.setPassword("segura123");
        request.setNombre("Nuevo Usuario");
        request.setRole(Role.USER);

        when(usuarioRepository.existsByUsername("nuevousuario")).thenReturn(false);
        when(usuarioRepository.existsByEmail("nuevo@mail.cl")).thenReturn(false);
        when(passwordEncoder.encode("segura123")).thenReturn("$2a$10$encodedPass");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSample);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("eyJhbGc.nuevo.token");

        LoginResponse respuesta = authService.register(request);

        assertThat(respuesta.getToken()).isEqualTo("eyJhbGc.nuevo.token");
        assertThat(respuesta.getMessage()).isEqualTo("Registro exitoso");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("register() lanza NegocioException cuando el username ya existe")
    void register_lanzaExcepcionConUsernameDuplicado() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("juan123");
        request.setEmail("otro@mail.cl");
        request.setPassword("pass");
        request.setNombre("Otro");

        when(usuarioRepository.existsByUsername("juan123")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("username ya existe");
    }

    @Test
    @DisplayName("register() lanza NegocioException cuando el email ya está registrado")
    void register_lanzaExcepcionConEmailDuplicado() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("usuarionuevo");
        request.setEmail("juan@mail.cl");
        request.setPassword("pass");
        request.setNombre("Nuevo");

        when(usuarioRepository.existsByUsername("usuarionuevo")).thenReturn(false);
        when(usuarioRepository.existsByEmail("juan@mail.cl")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(NegocioException.class)
                .hasMessageContaining("email ya está registrado");
    }

    @Test
    @DisplayName("validateToken() retorna valid=true para un token válido")
    void validateToken_retornaTrueCuandoTokenEsValido() {
        when(jwtService.validateToken("tokenValido")).thenReturn(true);
        when(jwtService.extractUsername("tokenValido")).thenReturn("juan123");
        when(jwtService.extractRole("tokenValido")).thenReturn("USER");

        TokenValidationResponse respuesta = authService.validateToken("tokenValido");

        assertThat(respuesta.isValid()).isTrue();
        assertThat(respuesta.getUsername()).isEqualTo("juan123");
        assertThat(respuesta.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("validateToken() retorna valid=false para un token inválido")
    void validateToken_retornaFalseCuandoTokenEsInvalido() {
        when(jwtService.validateToken("tokenInvalido")).thenReturn(false);

        TokenValidationResponse respuesta = authService.validateToken("tokenInvalido");

        assertThat(respuesta.isValid()).isFalse();
        assertThat(respuesta.getUsername()).isNull();
    }
}