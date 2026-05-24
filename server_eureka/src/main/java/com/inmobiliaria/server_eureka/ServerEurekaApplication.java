package com.inmobiliaria.server_eureka; // Define el paquete donde vive esta clase

// ============================================================
// SERVIDOR EUREKA - REGISTRO DE MICROSERVICIOS
// ============================================================
// Este es el "directorio telefónico" de todos los microservicios.
// Cuando un microservicio arranca, se registra aquí con su nombre
// y dirección (host:puerto). Así los demás servicios pueden
// encontrarse sin hardcodear IPs o puertos.
//
// Arranca en: http://localhost:8761
// Puedes ver todos los servicios registrados en esa URL.
// ============================================================

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

// @SpringBootApplication: Le dice a Spring Boot que esta es la clase principal
// de la aplicación. Activa la autoconfiguración, el escaneo de componentes
// y permite declarar @Bean dentro del mismo archivo si es necesario.
@SpringBootApplication

// @EnableEurekaServer: Convierte esta aplicación en un Servidor Eureka.
// Sin esta anotación sería solo una app Spring Boot normal.
// Con ella, levanta el panel web de Eureka en http://localhost:8761
@EnableEurekaServer
public class ServerEurekaApplication {

    // main() es el punto de entrada de cualquier aplicación Java.
    // SpringApplication.run() levanta todo el contexto de Spring Boot:
    // carga configuraciones, inicia el servidor web, conecta a BD, etc.
    public static void main(String[] args) {
        SpringApplication.run(ServerEurekaApplication.class, args);
    }
}