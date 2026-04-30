package com.inmobiliaria.authservice.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {


    private static final Logger log =
            LoggerFactory.getLogger(JwtUtil.class);


    @Value("${jwt.secret}")
    private String secret;


    @Value("${jwt.expiration}")
    private Long expiration;


    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email, String rol) {
        log.info("Generando token JWT para: {}", email);

        return Jwts.builder()
                // Subject = identificador principal (email)
                .setSubject(email)
                // Claim personalizado para guardar el rol
                .claim("rol", rol)
                // Fecha de creacion del token
                .setIssuedAt(new Date())
                // Fecha de expiracion del token
                .setExpiration(
                        new Date(System.currentTimeMillis() + expiration)
                )

                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }


    public String getRolFromToken(String token) {
        return getClaims(token).get("rol", String.class);
    }


    public boolean validateToken(String token) {
        try {
            getClaims(token);
            log.info("Token JWT valido");
            return true;
        } catch (Exception e) {
            log.warn("Token JWT invalido: {}", e.getMessage());
            return false;
        }
    }


    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}



