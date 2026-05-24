package com.inmobiliaria.usuarioservice; // Paquete raíz del microservicio

// ============================================================
// USUARIO SERVICE - MICROSERVICIO DE PERFILES DE USUARIO
// ============================================================
// Gestiona los perfiles de los usuarios del sistema inmobiliario.
// Un "usuario" en este servicio es una persona (arrendatario o
// propietario) con su información de contacto y perfil.
//
// DIFERENCIA con auth-service:
//   auth-service  → credenciales de acceso (username, password)
//   usuario-service → información del perfil (nombre, teléfono, ciudad)
//
// Puerto: 8082
// Base de datos: usuario_db
// Registrado en Eureka como: "usuario-service"
// ============================================================

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication  // activa Spring Boot: autoconfiguración + escaneo de componentes
@EnableDiscoveryClient  // registra este servicio en Eureka al iniciar
public class UsuarioServiceApplication {

    // Método principal: arranca el microservicio.
    public static void main(String[] args) {
        SpringApplication.run(UsuarioServiceApplication.class, args);
    }
}