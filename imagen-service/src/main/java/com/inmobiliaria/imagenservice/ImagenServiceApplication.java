package com.inmobiliaria.imagenservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.inmobiliaria.imagenservice"})
@EnableDiscoveryClient
public class ImagenServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImagenServiceApplication.class, args);
    }
}
