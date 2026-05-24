package com.inmobiliaria.resenaservice; // Paquete raíz del microservicio

// ============================================================
// RESEÑA SERVICE - MICROSERVICIO DE EVALUACIONES
// ============================================================
// Gestiona las reseñas y calificaciones de propiedades.
// Los arrendatarios pueden evaluar las propiedades que arrendaron.
//
// Dependencias externas (via Feign):
//   → propiedad-service: verifica que la propiedad existe antes de crear reseña
//
// Puerto: 8084
// Base de datos: resena_db
// Registrado en Eureka como: "resena-service"
// ============================================================

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication  // activa autoconfiguración y escaneo de componentes
@EnableDiscoveryClient  // registra en Eureka
@EnableFeignClients     // activa los @FeignClient (PropiedadClient)
public class ResenaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResenaServiceApplication.class, args);
    }
}