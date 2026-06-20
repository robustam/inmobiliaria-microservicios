package com.inmobiliaria.reservaservice; // Paquete raíz del microservicio

// ============================================================
// RESERVA SERVICE - MICROSERVICIO DE GESTIÓN DE RESERVAS
// ============================================================
// Gestiona el proceso de arriendo: conecta arrendatarios con propiedades.
//
// Responsabilidades:
//   - Crear y gestionar reservas de arriendo
//   - Verificar disponibilidad de propiedades (via Feign → propiedad-service)
//   - Cambiar estado de propiedades al reservar/liberar (via Feign)
//   - Mantener historial de arriendos
//
// Dependencias externas (via Feign):
//   → propiedad-service: verifica disponibilidad y cambia estado
//
// Puerto: 8083
// Base de datos: reserva_db
// Registrado en Eureka como: "reserva-service"
// ============================================================

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.inmobiliaria.reservaservice"})
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class ReservaServiceApplication {

    // Arranque del microservicio.
    public static void main(String[] args) {
        SpringApplication.run(ReservaServiceApplication.class, args);
    }
}