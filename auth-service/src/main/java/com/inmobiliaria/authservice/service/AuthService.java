package com.inmobiliaria.authservice.service;

import com.inmobiliaria.authservice.dto.request.RegisterRequest;
import com.inmobiliaria.authservice.dto.response.AuthResponse;
import com.inmobiliaria.authservice.model.Usuario;
import com.inmobiliaria.authservice.repository.UsuarioRepository;
import com.inmobiliaria.authservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.inmobiliaria.authservice.dto.request.LoginRequest;



@Service
@RequiredArgsConstructor
public class AuthService {


    // Logger para trazabilidad de operaciones
    private static final Logger log =
            LoggerFactory.getLogger(AuthService.class);

    // Acceso a la base de datos de usuarios
    private final UsuarioRepository usuarioRepository;

    // Para encriptar y verificar passwords
    private final PasswordEncoder passwordEncoder;

    // Para generar y validar tokens JWT
    private final JwtUtil jwtUtil;

    // Registra un nuevo usuario en el sistema
    public AuthResponse register(RegisterRequest request) {

        log.info("Iniciando registro para email: {}",
                request.getEmail());

        try {
            // Verifica que el email no este registrado
            if (usuarioRepository.existsByEmail(
                    request.getEmail())) {
                log.warn("Email ya registrado: {}",
                        request.getEmail());
                throw new RuntimeException(
                        "El email ya esta registrado");
            }

            // Verifica que el rut no este registrado
            if (usuarioRepository.existsByRut(
                    request.getRut())) {
                log.warn("RUT ya registrado: {}",
                        request.getRut());
                throw new RuntimeException(
                        "El RUT ya esta registrado");
            }

            // Crea el nuevo usuario
            Usuario usuario = new Usuario();
            usuario.setRut(request.getRut());
            usuario.setEmail(request.getEmail());

            // Encripta el password antes de guardar
            usuario.setPassword(
                    passwordEncoder.encode(request.getPassword()));
            // LÍNEA CORRECTA (Reemplázala aquí):
            usuario.setRol(Usuario.Rol.valueOf(request.getRol().toUpperCase()));
            usuario.setActivo(true);

            // Guarda en MySQL
            usuarioRepository.save(usuario);

            log.info("Usuario registrado exitosamente: {}",
                    request.getEmail());

            // Genera el token JWT
            String token = jwtUtil.generateToken(
                    usuario.getEmail(),
                    usuario.getRol().name());

            return new AuthResponse(
                    token,
                    usuario.getRut(),
                    usuario.getEmail(),
                    usuario.getRol().name(),
                    "Usuario registrado correctamente");

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado en registro: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al registrar el usuario");
        }
    }

    // Autentica un usuario y retorna su token JWT
    public AuthResponse login(LoginRequest request) {

        log.info("Intento de login para: {}",
                request.getEmail());

        try {
            // Busca el usuario por email
            Usuario usuario = usuarioRepository
                    .findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        log.warn("Login fallido — email no encontrado: {}",
                                request.getEmail());
                        return new RuntimeException(
                                "Credenciales incorrectas ");
                    });

            // Verifica el password
            if (!passwordEncoder.matches(
                    request.getPassword(),
                    usuario.getPassword())) {
                log.warn("Login fallido — password incorrecto: {}",
                        request.getEmail());
                throw new RuntimeException(
                        "Credenciales incorrectas");
            }

            // Verifica que el usuario este activo
            if (!usuario.getActivo()) {
                log.warn("Login fallido — usuario inactivo: {}",
                        request.getEmail());
                throw new RuntimeException("Usuario inactivo");
            }

            // Genera el token JWT
            String token = jwtUtil.generateToken(
                    usuario.getEmail(),
                    usuario.getRol().name());

            log.info("Login exitoso para: {}",
                    request.getEmail());

            return new AuthResponse(
                    token,
                    usuario.getRut(),
                    usuario.getEmail(),
                    usuario.getRol().name(),
                    "Login exitoso");

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado en login: {}",
                    e.getMessage());
            throw new RuntimeException(
                    "Error al iniciar sesion");
        }
    }

    // Valida si un token JWT es correcto
    public boolean validateToken(String token) {
        log.info("Validando token JWT");
        return jwtUtil.validateToken(token);
    }



}
