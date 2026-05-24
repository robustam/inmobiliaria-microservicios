package com.inmobiliaria.authservice.service; // Paquete de servicios del auth-service

// ============================================================
// SERVICIO DE AUTENTICACIÓN - LÓGICA DE NEGOCIO
// ============================================================
// Este servicio contiene la lógica de:
//   1. LOGIN:    verificar credenciales y generar JWT
//   2. REGISTER: crear nuevo usuario en la base de datos
//   3. VALIDATE: verificar si un token JWT es válido
//
// AuthService es el "cerebro" de la autenticación.
// El Controller recibe la petición HTTP y la delega aquí.
// Este servicio habla con la BD (via UsuarioRepository)
// y con el servicio JWT (via JwtService).
// ============================================================

import com.inmobiliaria.authservice.dto.*;                    // DTOs de entrada/salida
import com.inmobiliaria.authservice.exception.NegocioException;             // Error de regla de negocio
import com.inmobiliaria.authservice.exception.RecursoNoEncontradoException; // Error 404
import com.inmobiliaria.authservice.model.Usuario;                          // Entidad de usuario
import com.inmobiliaria.authservice.model.Usuario.Role;                     // Enum de roles (inner class)
import com.inmobiliaria.authservice.repository.UsuarioRepository;           // Acceso a la BD
import lombok.RequiredArgsConstructor; // Genera constructor con los campos final
import lombok.extern.slf4j.Slf4j;     // Activa el logger automático (log.info, log.debug, etc.)
import org.springframework.security.crypto.password.PasswordEncoder; // Codifica/verifica contraseñas
import org.springframework.stereotype.Service; // Marca como servicio Spring

// @Slf4j: genera automáticamente una variable "log" de tipo Logger.
// Permite registrar mensajes en la consola con niveles: DEBUG, INFO, WARN, ERROR.
// Equivale a escribir: private static final Logger log = LoggerFactory.getLogger(AuthService.class);
@Slf4j

// @Service: registra esta clase en el contexto de Spring.
// Puede ser inyectada en otros componentes (como el Controller).
@Service

// @RequiredArgsConstructor: genera un constructor con todos los campos "final".
// Es la forma moderna de hacer inyección de dependencias (en lugar de @Autowired).
// Spring detecta el constructor y le inyecta los beans automáticamente.
@RequiredArgsConstructor
public class AuthService {

    // final: estas dependencias se inyectan por constructor y no cambian.
    // Spring busca beans de estos tipos y los inyecta al crear AuthService.

    // UsuarioRepository: acceso a la tabla auth_usuarios en MySQL.
    private final UsuarioRepository usuarioRepository;

    // JwtService: genera y valida tokens JWT.
    private final JwtService jwtService;

    // PasswordEncoder: cifra contraseñas con BCrypt y verifica si coinciden.
    // BCrypt es un algoritmo de hash seguro: nunca se puede revertir.
    // Configurado como @Bean en alguna clase de configuración de Spring Security.
    private final PasswordEncoder passwordEncoder;

    // ============================================================
    // MÉTODO: login()
    // ============================================================
    // Verifica las credenciales del usuario y retorna un token JWT.
    //
    // Flujo:
    //   1. Busca el usuario en la BD por username
    //   2. Compara la contraseña enviada con el hash guardado en BD
    //   3. Si todo es correcto, genera un token JWT con username y rol
    //   4. Retorna el token junto con los datos del usuario
    //
    // Parámetros:
    //   username = nombre de usuario (ej: "juan123")
    //   password = contraseña en texto plano (ej: "miPassword123")
    // ============================================================
    public LoginResponse login(String username, String password) {
        // log.info(): registra un mensaje de nivel INFO (operaciones normales importantes).
        // {} es un placeholder que se reemplaza con el valor del argumento.
        log.info("Intento de login para usuario: {}", username);

        // findByUsername() retorna Optional<Usuario>.
        // orElseThrow() lanza excepción si el Optional está vacío (usuario no existe).
        // RecursoNoEncontradoException → GlobalExceptionHandler → HTTP 404
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        // passwordEncoder.matches(rawPassword, encodedPassword):
        //   rawPassword    = contraseña que envió el usuario en texto plano
        //   encodedPassword = hash BCrypt guardado en la BD
        // Retorna true si la contraseña es correcta, false si no.
        // NUNCA guardamos contraseñas en texto plano, solo el hash.
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            // log.warn(): nivel de advertencia. Indica algo sospechoso (intento fallido).
            log.warn("Credenciales incorrectas para usuario: {}", username);
            // NegocioException → GlobalExceptionHandler → HTTP 400
            throw new NegocioException("Credenciales incorrectas");
        }

        // usuario.getRole().name(): convierte el enum Role a String ("USER", "ADMIN", etc.)
        String token = jwtService.generateToken(usuario.getUsername(), usuario.getRole().name());
        log.info("Login exitoso para usuario: {} con rol: {}", username, usuario.getRole());

        // LoginResponse es un DTO de respuesta con: token, username, role, mensaje.
        return new LoginResponse(token, usuario.getUsername(), usuario.getRole().name(), "Login exitoso");
    }

    // ============================================================
    // MÉTODO: register()
    // ============================================================
    // Crea un nuevo usuario en la base de datos.
    //
    // Flujo:
    //   1. Verifica que el username no esté tomado
    //   2. Verifica que el email no esté registrado
    //   3. Crea el objeto Usuario con contraseña hasheada
    //   4. Guarda en la BD
    //   5. Genera token JWT y retorna respuesta
    //
    // Parámetros:
    //   request = DTO con todos los datos del nuevo usuario
    // ============================================================
    public LoginResponse register(RegisterRequest request) {
        log.info("Registrando nuevo usuario: {}", request.getUsername());

        // existsByUsername(): consulta si ya hay un usuario con ese username.
        // Si es true, lanzamos NegocioException (regla de negocio: username debe ser único).
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new NegocioException("El username ya existe");
        }

        // Misma verificación para el email.
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new NegocioException("El email ya está registrado");
        }

        // Usuario.builder(): usa el patrón Builder (generado por @Builder en Usuario).
        // Construye el objeto campo por campo con sintaxis fluente.
        Usuario usuario = Usuario.builder()
                .username(request.getUsername())    // nombre de usuario
                .email(request.getEmail())          // correo electrónico
                // passwordEncoder.encode(): convierte la contraseña a hash BCrypt.
                // Ejemplo: "miPass123" → "$2a$10$xH8kLmN..."
                // Este hash se guarda en la BD. Nunca se guarda la contraseña original.
                .password(passwordEncoder.encode(request.getPassword()))
                .nombre(request.getNombre())        // nombre real (puede ser null)
                // Si el request trae un rol, lo usamos. Si no, asignamos USER por defecto.
                // Operador ternario: condicion ? valorSiTrue : valorSiFalse
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .build(); // construye el objeto Usuario final

        // save(): INSERT en la tabla auth_usuarios. Spring Data JPA genera el SQL.
        usuarioRepository.save(usuario);

        // Generamos el token JWT para que el usuario pueda empezar a usarlo inmediatamente.
        String token = jwtService.generateToken(usuario.getUsername(), usuario.getRole().name());
        log.info("Usuario registrado exitosamente: {} con rol: {}", usuario.getUsername(), usuario.getRole());

        return new LoginResponse(token, usuario.getUsername(), usuario.getRole().name(), "Registro exitoso");
    }

    // ============================================================
    // MÉTODO: validateToken()
    // ============================================================
    // Verifica si un token JWT es válido y retorna la información del usuario.
    // Usado por el API Gateway para verificar peticiones entrantes.
    //
    // Parámetros:
    //   token = el String JWT que viene en el header de la petición
    // ============================================================
    public TokenValidationResponse validateToken(String token) {
        // log.debug(): nivel detallado. Solo aparece en consola si el nivel está en DEBUG.
        log.debug("Validando token JWT");

        // jwtService.validateToken(): verifica firma y expiración del token.
        if (jwtService.validateToken(token)) {
            log.debug("Token válido para usuario: {}", jwtService.extractUsername(token));
            // TokenValidationResponse: DTO con isValid=true, username, role, mensaje.
            return new TokenValidationResponse(true,
                    jwtService.extractUsername(token), // extrae el campo "sub" del token
                    jwtService.extractRole(token),     // extrae el claim "role" del token
                    "Token válido");
        }

        log.warn("Token inválido o expirado");
        // Token inválido: retornamos false con username y role null.
        return new TokenValidationResponse(false, null, null, "Token inválido o expirado");
    }
}