package com.inmobiliaria.reporteservice; // Paquete raíz del microservicio

// ============================================================
// REPORTE SERVICE - MICROSERVICIO DE REPORTES Y ESTADÍSTICAS
// ============================================================
// Genera reportes estadísticos del sistema inmobiliario para administradores.
//
// Dependencias externas (via Feign):
//   → propiedad-service: obtiene estadísticas de propiedades
//   → reserva-service:   obtiene estadísticas de reservas e ingresos
//
// Puerto: 8085
// Base de datos: reporte_db (historial de reportes generados)
// Registrado en Eureka como: "reporte-service"
// ============================================================

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.inmobiliaria.reporteservice"})
@EnableDiscoveryClient // registra en Eureka
@EnableFeignClients    // activa PropiedadClient y ReservaClient (Feign)
public class ReporteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReporteServiceApplication.class, args);
    }
}