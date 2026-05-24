package com.inmobiliaria.propiedadservice; // Paquete raíz del microservicio

// ============================================================
// PROPIEDAD SERVICE - MICROSERVICIO DE PROPIEDADES
// ============================================================
// Este microservicio es el NÚCLEO del sistema inmobiliario.
// Gestiona todas las propiedades (casas y departamentos) disponibles
// para arriendo en Chile.
//
// Responsabilidades:
//   - CRUD de propiedades (crear, leer, actualizar, desactivar)
//   - Búsqueda y filtrado por región, comuna, tipo, precio
//   - Gestión de estados: DISPONIBLE, ARRENDADA, INACTIVA
//   - Ser consultado por reserva-service, resena-service,
//     busqueda-service y reporte-service via Feign
//
// Puerto: 8086
// Base de datos: propiedad_db
// Registrado en Eureka como: "propiedad-service"
// ============================================================

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// @SpringBootApplication: activa la autoconfiguración de Spring Boot,
// el escaneo de componentes y el contexto de aplicación.
// scanBasePackages: indica explícitamente qué paquete escanear para
// encontrar @Service, @Repository, @RestController, etc.
@SpringBootApplication(scanBasePackages = {"com.inmobiliaria.propiedadservice"})

// @EnableDiscoveryClient: registra este servicio en Eureka al iniciar.
// Otros microservicios pueden encontrarlo por su nombre "propiedad-service".
@EnableDiscoveryClient
public class PropiedadServiceApplication {

    // Punto de entrada de la aplicación Java.
    // SpringApplication.run() arranca el servidor embebido (Tomcat),
    // carga la configuración, conecta a MySQL y registra en Eureka.
    public static void main(String[] args) {
        SpringApplication.run(PropiedadServiceApplication.class, args);
    }
}