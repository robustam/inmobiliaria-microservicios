package com.inmobiliaria.authservice.service; // Paquete de servicios del auth-service

// ============================================================
// SERVICIO JWT - GENERACIÓN Y VALIDACIÓN DE TOKENS
// ============================================================
// JWT = JSON Web Token: es un estándar para transmitir información
// de forma segura entre partes como un objeto JSON firmado.
//
// Estructura de un JWT (separado por puntos):
//   HEADER.PAYLOAD.SIGNATURE
//   eyJhbGc... . eyJ1c2Vy... . SflKxwRJ...
//
// PAYLOAD contiene los "claims" (datos del usuario):
//   { "sub": "juan123", "role": "USER", "exp": 1716000000 }
//
// La FIRMA (HMAC-SHA256) garantiza que nadie modificó el token.
// Solo quien tiene la clave secreta puede crear o verificar tokens.
//
// Flujo:
//   Login → generarToken → cliente guarda el token
//   Petición → envía token → validateToken → acceso permitido
// ============================================================

import io.jsonwebtoken.*;                    // Librería jjwt para manipular tokens JWT
import io.jsonwebtoken.security.Keys;        // Utilidad para crear claves criptográficas
import org.springframework.beans.factory.annotation.Value; // Inyecta valores de properties
import org.springframework.stereotype.Service; // Marca como componente de servicio Spring

import java.nio.charset.StandardCharsets; // Codificación de caracteres (UTF-8)
import java.security.Key;                  // Interfaz para claves criptográficas
import java.util.Date;                     // Fecha/hora para expiración del token
import java.util.HashMap;                  // Mapa para guardar claims adicionales
import java.util.Map;                      // Interfaz Map

// @Service: registra esta clase en el contexto de Spring como un servicio.
// Puede ser inyectada con @Autowired o a través de constructores en otras clases.
@Service
public class JwtService {

    // @Value("${jwt.secret}"): inyecta el valor de la propiedad "jwt.secret"
    // del archivo application.properties en esta variable.
    // Así no hardcodeamos la clave secreta en el código fuente.
    @Value("${jwt.secret}")
    private String secret; // clave secreta para firmar los tokens (debe ser larga y segura)

    // @Value("${jwt.expiration}"): inyecta el tiempo de vida del token en milisegundos.
    // 86400000 ms = 24 horas. Pasado ese tiempo el token expira y hay que hacer login de nuevo.
    @Value("${jwt.expiration}")
    private long expiration; // tiempo de expiración en milisegundos

    // Construye la clave criptográfica HMAC a partir de la cadena secreta.
    // Keys.hmacShaKeyFor() crea una clave compatible con el algoritmo HS256.
    // Se convierte el String a bytes con codificación UTF-8.
    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Genera un nuevo token JWT firmado con los datos del usuario.
    // Parámetros:
    //   username = identificador del usuario (el "subject" del token)
    //   role     = rol del usuario (USER, ADMIN, PROPIETARIO)
    public String generateToken(String username, String role) {
        // Map para los claims adicionales (datos extra que van en el token).
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // agrega el rol como claim personalizado

        return Jwts.builder()              // inicia la construcción del token
                .setClaims(claims)         // agrega los claims (role en este caso)
                .setSubject(username)      // "sub" = sujeto del token (quién es el usuario)
                .setIssuedAt(new Date())   // "iat" = fecha de emisión (ahora)
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // "exp" = fecha de expiración
                .signWith(getKey(), SignatureAlgorithm.HS256) // firma con HMAC-SHA256
                .compact(); // serializa todo a String (el token JWT final)
    }

    // Verifica si un token JWT es válido (firma correcta y no expirado).
    // Retorna true si es válido, false si es inválido o expirado.
    public boolean validateToken(String token) {
        try {
            // parserBuilder() + setSigningKey() verifica la firma del token.
            // parseClaimsJws() lanza excepción si la firma es inválida o el token expiró.
            Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);
            return true; // si llegó aquí sin excepción, el token es válido
        } catch (JwtException | IllegalArgumentException e) {
            // JwtException: token malformado, firma inválida o expirado
            // IllegalArgumentException: token es null o vacío
            return false;
        }
    }

    // Extrae el username (subject) del token JWT.
    // Se usa para saber QUÉ usuario está haciendo la petición.
    public String extractUsername(String token) {
        return getClaims(token).getSubject(); // getSubject() retorna el campo "sub"
    }

    // Extrae el rol del usuario desde los claims del token.
    // Se usa para verificar permisos (ej: solo ADMIN puede ver reportes).
    public String extractRole(String token) {
        return (String) getClaims(token).get("role"); // obtiene el claim "role"
    }

    // Método privado auxiliar: parsea el token y retorna todos sus claims.
    // Claims = cuerpo del JWT con todos los datos almacenados.
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey()) // usa la misma clave para verificar la firma
                .build()
                .parseClaimsJws(token)   // parsea y valida el token
                .getBody();              // retorna el cuerpo (claims) del token
    }
}