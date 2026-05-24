package com.inmobiliaria.notificacionservice; // Paquete raíz del microservicio

// ============================================================
// NOTIFICACIÓN SERVICE - MICROSERVICIO DE NOTIFICACIONES
// ============================================================
// Gestiona el sistema de notificaciones de la plataforma.
// Envía mensajes a usuarios sobre reservas, reseñas, pagos, etc.
//
// Puerto: 8087
// Base de datos: notificacion_db
// Registrado en Eureka como: "notificacion-service"
// ============================================================

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// scanBasePackages: indica a Spring qué paquete escanear para encontrar componentes.
@SpringBootApplication(scanBasePackages = {"com.inmobiliaria.notificacionservice"})
@EnableDiscoveryClient // registra en Eureka al iniciar
public class NotificacionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificacionServiceApplication.class, args);
    }
}