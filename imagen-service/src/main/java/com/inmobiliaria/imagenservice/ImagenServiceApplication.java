package com.inmobiliaria.imagenservice; // Paquete raíz del microservicio

// ============================================================
// IMAGEN SERVICE - MICROSERVICIO DE GESTIÓN DE IMÁGENES
// ============================================================
// Gestiona los metadatos de las imágenes/fotos de propiedades.
// Permite subir, listar, establecer foto principal y eliminar imágenes.
//
// NOTA: Almacena la URL y metadatos de cada imagen, no el archivo binario.
// Las imágenes reales estarían en un servidor de archivos o AWS S3.
//
// Puerto: 8088
// Base de datos: imagen_db
// Registrado en Eureka como: "imagen-service"
// ============================================================

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.inmobiliaria.imagenservice"})
@EnableDiscoveryClient // registra en Eureka para ser encontrado por el Gateway
public class ImagenServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImagenServiceApplication.class, args);
    }
}