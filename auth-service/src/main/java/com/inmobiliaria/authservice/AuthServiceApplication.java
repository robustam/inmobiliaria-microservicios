package com.inmobiliaria.authservice; // Paquete raíz del microservicio de autenticación

// ============================================================
// AUTH SERVICE - MICROSERVICIO DE AUTENTICACIÓN Y SEGURIDAD
// ============================================================
// Este microservicio gestiona:
//   1. REGISTRO de nuevos usuarios (POST /api/v1/auth/register)
//   2. LOGIN de usuarios existentes (POST /api/v1/auth/login)
//   3. VALIDACIÓN de tokens JWT (GET  /api/v1/auth/validate)
//
// Genera tokens JWT (JSON Web Token) que los demás microservicios
// pueden usar para identificar quién hace cada petición.
//
// Puerto: 8081
// Base de datos: auth_db
// ============================================================

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// @SpringBootApplication: punto de entrada de la aplicación Spring Boot.
@SpringBootApplication

// @EnableDiscoveryClient: registra este servicio en el servidor Eureka
// con el nombre "auth-service" para que el Gateway pueda encontrarlo.
@EnableDiscoveryClient
public class AuthServiceApplication {

    // Método principal: arranca el microservicio completo.
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}