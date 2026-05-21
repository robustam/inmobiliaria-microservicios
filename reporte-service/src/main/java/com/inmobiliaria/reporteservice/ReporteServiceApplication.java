package com.inmobiliaria.reporteservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.inmobiliaria.reporteservice"})
@EnableDiscoveryClient
public class ReporteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReporteServiceApplication.class, args);
    }
}
