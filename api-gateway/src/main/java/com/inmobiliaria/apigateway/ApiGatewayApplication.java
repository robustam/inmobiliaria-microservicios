package com.inmobiliaria.apigateway; // Paquete raíz del API Gateway

// ============================================================
// API GATEWAY - PUERTA DE ENTRADA ÚNICA AL SISTEMA
// ============================================================
// El API Gateway es el único punto de contacto para el cliente
// (Postman, app móvil, navegador). Recibe todas las peticiones
// HTTP y las redirige al microservicio correcto según la ruta.
//
// Ejemplo de flujo:
//   Postman → POST /api/v1/auth/login
//       → Gateway detecta que empieza con /api/v1/auth/**
//       → Redirige a auth-service (puerto 8081)
//       → auth-service procesa y responde
//       → Gateway devuelve la respuesta al cliente
//
// Puerto: 8080 (todas las peticiones entran por aquí)
// ============================================================

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// @SpringBootApplication: Marca el punto de entrada de la app Spring Boot.
// Combina @Configuration + @EnableAutoConfiguration + @ComponentScan.
@SpringBootApplication

// @EnableDiscoveryClient: Conecta este gateway a Eureka para poder
// descubrir los microservicios por nombre (ej: "auth-service")
// sin necesidad de hardcodear IPs o puertos.
@EnableDiscoveryClient
public class ApiGatewayApplication {

    // Método principal que inicia toda la aplicación.
    // SpringApplication.run() arranca el servidor web (Netty, no Tomcat)
    // y carga todas las rutas definidas en application.properties.
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}