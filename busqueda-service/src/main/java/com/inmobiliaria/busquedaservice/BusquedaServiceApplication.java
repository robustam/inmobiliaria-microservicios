package com.inmobiliaria.busquedaservice; // Paquete raíz del microservicio

// ============================================================
// BÚSQUEDA SERVICE - MICROSERVICIO DE BÚSQUEDA DE ARRIENDOS
// ============================================================
// Servicio especializado en búsqueda y filtrado de propiedades.
// NO tiene base de datos propia: todo viene de propiedad-service via Feign.
//
// Actúa como PROXY/AGREGADOR:
//   Cliente → busqueda-service → propiedad-service (Feign)
//                             ↓ aplica filtros adicionales
//                             ↓ retorna resultados filtrados
//
// Dependencias externas (via Feign):
//   → propiedad-service: fuente de todas las propiedades
//
// Puerto: 8089
// Registrado en Eureka como: "busqueda-service"
// ============================================================

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.inmobiliaria.busquedaservice"})
@EnableDiscoveryClient  // registra en Eureka
@EnableFeignClients     // activa PropiedadClient (Feign)
public class BusquedaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusquedaServiceApplication.class, args);
    }
}