package com.inmobiliaria.busquedaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.inmobiliaria.busquedaservice"})
@EnableDiscoveryClient
public class BusquedaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusquedaServiceApplication.class, args);
    }
}

